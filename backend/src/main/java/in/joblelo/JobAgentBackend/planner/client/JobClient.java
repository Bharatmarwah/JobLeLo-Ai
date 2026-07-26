package in.joblelo.JobAgentBackend.planner.client;

import in.joblelo.JobAgentBackend.planner.builder.AdzunaCountryCodeResolver;
import in.joblelo.JobAgentBackend.planner.model.*;
import in.joblelo.JobAgentBackend.planner.model.adzuna.AdzunaResponse;
import in.joblelo.JobAgentBackend.planner.model.jooble.JoobleRequest;
import in.joblelo.JobAgentBackend.planner.model.jooble.JoobleResponse;
import in.joblelo.JobAgentBackend.planner.model.remotive.RemotiveResponse;
import in.joblelo.JobAgentBackend.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final WebClient.Builder webClientBuilder;
    private final AdzunaCountryCodeResolver countryCodeResolver;
    private final ValidationUtils validationUtils;

    @Value("${adzuna.app.id}")
    private String adzunaAppId;

    @Value("${adzuna.app.key}")
    private String adzunaAppKey;

    @Value("${jooble.api.key}")
    private String joobleApiKey;


    public AdzunaResponse adzunaJobSearch(ToolSchema toolSchema) {

        validationUtils.requireNonBlank(adzunaAppId, adzunaAppKey);

        String countryCode = countryCodeResolver.resolve(toolSchema.getLocation());

        log.info("[Adzuna] Searching: role='{}', location='{}', country='{}'",
                toolSchema.getRole(), toolSchema.getLocation(), countryCode);

        return webClientBuilder.clone()
                .baseUrl("https://api.adzuna.com")
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/api/jobs/{country}/search/1")
                        .queryParam("app_id", adzunaAppId)
                        .queryParam("app_key", adzunaAppKey)
                        .queryParam("what", toolSchema.getRole())
                        .queryParam("where", toolSchema.getLocation())
                        .queryParam("results_per_page", 50)
                        .build(countryCode))
                .exchangeToMono(response -> {
                    log.debug("[Adzuna] Response status: {}", response.statusCode());

                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                if (response.statusCode().is2xxSuccessful()) {
                                    try {
                                        com.fasterxml.jackson.databind.ObjectMapper mapper =
                                                new com.fasterxml.jackson.databind.ObjectMapper();
                                        AdzunaResponse result =
                                                mapper.readValue(body, AdzunaResponse.class);
                                        log.debug("[Adzuna] Parsed response body ({} chars)", body.length());
                                        return Mono.just(result);
                                    } catch (Exception ex) {
                                        log.error("[Adzuna] Failed to parse response: {}", ex.getMessage());
                                        return Mono.error(ex);
                                    }
                                }
                                log.warn("[Adzuna] Non-2xx response: {} — body snippet: {}",
                                        response.statusCode(), body.substring(0, Math.min(body.length(), 200)));
                                return Mono.error(
                                        new RuntimeException(
                                                "Adzuna HTTP " + response.statusCode()));
                            });
                })
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                        .filter(throwable -> {
                            String msg = throwable.getMessage();
                            return msg != null && (msg.contains("503") || msg.contains("502") || msg.contains("504"));
                        }))
                .doOnError(err -> log.warn("[Adzuna] Request failed after retries: {}", err.getMessage()))
                .block(REQUEST_TIMEOUT);
    }

    public JoobleResponse joobleJobSearch(ToolSchema toolSchema) {

        validationUtils.requireNonBlank(joobleApiKey);

        JoobleRequest request = JoobleRequest.builder()
                .keywords(toolSchema.getRole())
                .location(toolSchema.getLocation())
                .radius("0")              // explicit, not implicit — see caveat below
                .companysearch(false)     // explicit — force title/description match, not company name
                .resultOnPage(20)         // explicit — don't inherit Jooble's undocumented default
                .searchMode(0)            // explicit — documented default, but see caveat below
                .page(1)
                .build();

        log.info("[Jooble] Searching: role='{}', location='{}'",
                toolSchema.getRole(), toolSchema.getLocation());
        log.debug("[Jooble] Request body: keywords='{}', location='{}'",
                request.getKeywords(), request.getLocation());

        return webClientBuilder.clone()
                .baseUrl("https://jooble.org/api")
                .build()
                .post()
                .uri("/" + joobleApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .map(body -> {
                                    log.warn("[Jooble] API error — status: {}, body: {}",
                                            response.statusCode(),
                                            body.substring(0, Math.min(body.length(), 200)));
                                    return new RuntimeException("Jooble API Error : " + body);
                                })
                )
                .bodyToMono(JoobleResponse.class)
                .doOnSuccess(r -> log.info("[Jooble] Fetched {} total jobs", r != null ? r.getTotalCount() : 0))
                .doOnError(err -> log.warn("[Jooble] Request failed: {}", err.getMessage()))
                .block(REQUEST_TIMEOUT);
    }

    public RemotiveResponse remotiveJobSearch(ToolSchema toolSchema) {

        log.info("[Remotive] Searching: role='{}'", toolSchema.getRole());

        return webClientBuilder.clone()
                .baseUrl("https://remotive.com")
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/remote-jobs")
                        .queryParam("search", toolSchema.getRole())
                        .queryParam("limit",20)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .map(body -> {
                                    log.warn("[Remotive] API error — status: {}, body: {}",
                                            response.statusCode(),
                                            body.substring(0, Math.min(body.length(), 200)));
                                    return new RuntimeException("Remotive API Error : " + body);
                                })
                )
                .bodyToMono(RemotiveResponse.class)
                .doOnSuccess(r -> {
                    int count = r != null && r.getJobs() != null ? r.getJobs().size() : 0;
                    log.info("[Remotive] Fetched {} jobs", count);
                })
                .doOnError(err -> log.warn("[Remotive] Request failed: {}", err.getMessage()))
                .block(REQUEST_TIMEOUT);
    }

}