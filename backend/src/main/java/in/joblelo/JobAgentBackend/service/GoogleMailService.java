package in.joblelo.JobAgentBackend.service;

import in.joblelo.JobAgentBackend.entity.AuthUser;
import in.joblelo.JobAgentBackend.repository.AuthUserRepo;
import in.joblelo.JobAgentBackend.repository.GmailAccountRepo;
import in.joblelo.JobAgentBackend.requestdto.CreateUserGmailAccountRequest;
import in.joblelo.JobAgentBackend.responsedto.GmailConnectorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import in.joblelo.JobAgentBackend.exceptionhandling.ApiException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class GoogleMailService {

    @org.springframework.beans.factory.annotation.Value("${google.client.id}")
    private String googleClientId;
    @org.springframework.beans.factory.annotation.Value("${google.client.secret}")
    private String googleClientSecret;
    @org.springframework.beans.factory.annotation.Value("${gmail.redirect.uri}")
    private String gmailRedirectUri;

    private final AuthUserRepo authUserRepo;
    private final GmailAccountRepo gmailAccountRepo;

    private final UserService userService;

    private void validateGoogleConfig() {
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new ApiException("GOOGLE_CLIENT_ID is not configured. Please set it in .env file or system properties.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (googleClientSecret == null || googleClientSecret.isBlank()) {
            throw new ApiException("GOOGLE_CLIENT_SECRET is not configured. Please set it in .env file or system properties.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String connectGmail() {
        validateGoogleConfig();
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth";

        String scope = "https://www.googleapis.com/auth/gmail.readonly";

        return UriComponentsBuilder.fromHttpUrl(authUrl)
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", gmailRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("include_granted_scopes", "true")
                .build()
                .toUriString();
    }

    public Map<String, Object> gmailCallback(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri",
                gmailRedirectUri);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        HttpEntity<?> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<GmailConnectorResponse> response =
                restTemplate.postForEntity(
                        "https://oauth2.googleapis.com/token",
                        request,
                        GmailConnectorResponse.class
                );

        String accessToken = response.getBody().getAccessToken();
        String refreshToken = response.getBody().getRefreshToken();
        Long expiresIn = response.getBody().getAccessTokenExpiry();

        System.out.println(accessToken);
        System.out.println(refreshToken);

        CreateUserGmailAccountRequest gmailAccountRequest =
                new CreateUserGmailAccountRequest(
                        accessToken,
                        refreshToken,
                        expiresIn
                );

        userService.createUserGmailAccount(gmailAccountRequest);

        return Map.of("connected", "success");
    }

    public Map<String, Boolean> status() {
        String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        AuthUser user = authUserRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        boolean connected = gmailAccountRepo.findByUser(user).isPresent();

        return Map.of("connected", connected);
    }
}
