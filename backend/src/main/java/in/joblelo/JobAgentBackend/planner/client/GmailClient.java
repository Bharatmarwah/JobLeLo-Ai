package in.joblelo.JobAgentBackend.planner.client;

import in.joblelo.JobAgentBackend.planner.builder.GmailQueryBuilder;
import in.joblelo.JobAgentBackend.planner.model.gmail.GmailMessageDetail;
import in.joblelo.JobAgentBackend.planner.model.gmail.GmailMessageResponse;
import in.joblelo.JobAgentBackend.planner.model.ToolSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class GmailClient {

    private static final String BASE_URL =
            "https://gmail.googleapis.com/gmail/v1/users/me";

    private static final Duration TIMEOUT =
            Duration.ofSeconds(10);

    private final WebClient.Builder webClientBuilder;
    private final GmailQueryBuilder gmailQueryBuilder;

    /**
     * Fetch matching Gmail message ids.
     */
    public GmailMessageResponse listMessages(
            String accessToken,
            ToolSchema toolSchema
    ) {

        String query = gmailQueryBuilder.build(toolSchema);

        return webClientBuilder.clone()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/messages")
                        .queryParam("q", query)
                        .queryParam("maxResults", 5)
                        .build())
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GmailMessageResponse.class)
                .block(TIMEOUT);
    }

    /**
     * Fetch complete Gmail message.
     */
    public GmailMessageDetail getMessage(
            String accessToken,
            String messageId
    ) {

        return webClientBuilder.clone()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/messages/{id}")
                        .queryParam("format", "full")
                        .build(messageId))
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GmailMessageDetail.class)
                .block(TIMEOUT);
    }

}