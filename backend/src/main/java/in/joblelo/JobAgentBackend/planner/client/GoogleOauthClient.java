package in.joblelo.JobAgentBackend.planner.client;

import in.joblelo.JobAgentBackend.responsedto.GoogleAccessTokenResponse;
import in.joblelo.JobAgentBackend.utils.RefreshTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class GoogleOauthClient {

    private final WebClient.Builder webClientBuilder;

    @org.springframework.beans.factory.annotation.Value("${google.client.id}")
    private String googleClientId;
    @org.springframework.beans.factory.annotation.Value("${google.client.secret}")
    private String googleClientSecret;

    private final RefreshTokenUtil refreshTokenUtil;


    public GoogleAccessTokenResponse refreshAccessToken(
    ) {
        return webClientBuilder.clone()
                .baseUrl("https://oauth2.googleapis.com")
                .build()
                .post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(
                        new LinkedMultiValueMap<String, String>() {{
                            add("client_id", googleClientId);
                            add("client_secret", googleClientSecret);
                            add("refresh_token", refreshTokenUtil.getRefreshToken());
                            add("grant_type", "refresh_token");
                        }}
                )
                .retrieve()
                .bodyToMono(GoogleAccessTokenResponse.class)
                .block(Duration.ofSeconds(10));
    }
}