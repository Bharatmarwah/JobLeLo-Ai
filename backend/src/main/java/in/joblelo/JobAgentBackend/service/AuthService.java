package in.joblelo.JobAgentBackend.service;

import in.joblelo.JobAgentBackend.responsedto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import in.joblelo.JobAgentBackend.exceptionhandling.ApiException;
import org.springframework.http.HttpStatus;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @org.springframework.beans.factory.annotation.Value("${google.client.id}")
    private String googleClientId;
    @org.springframework.beans.factory.annotation.Value("${google.client.secret}")
    private String googleClientSecret;
    @org.springframework.beans.factory.annotation.Value("${github.client.id}")
    private String githubClientId;
    @org.springframework.beans.factory.annotation.Value("${github.client.secret}")
    private String githubClientSecret;

    private final UserService userService;

    private void validateGoogleConfig() {
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new ApiException("GOOGLE_CLIENT_ID is not configured. Please set it in .env file or system properties.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (googleClientSecret == null || googleClientSecret.isBlank()) {
            throw new ApiException("GOOGLE_CLIENT_SECRET is not configured. Please set it in .env file or system properties.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateGithubConfig() {
        if (githubClientId == null || githubClientId.isBlank()) {
            throw new ApiException("GITHUB_CLIENT_ID is not configured. Please set it in .env file or system properties.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (githubClientSecret == null || githubClientSecret.isBlank()) {
            throw new ApiException("GITHUB_CLIENT_SECRET is not configured. Please set it in .env file or system properties.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void googleConsentScreen(
            HttpServletResponse response
    ) {

        try {
            validateGoogleConfig();
            String redirectUri =
                    "http://localhost:8080/public/oauth/callback";

            String authUrl =
                    "https://accounts.google.com/o/oauth2/v2/auth";

            String url = UriComponentsBuilder
                    .fromHttpUrl(authUrl)
                    .queryParam("client_id", googleClientId)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("response_type", "code")
                    .queryParam("scope", "openid email profile")
                    .queryParam("access_type", "offline")
                    .queryParam("prompt", "consent")
                    .build()
                    .toUriString();

            response.sendRedirect(url);
        } catch (Exception e) {
            throw new ApiException("Failed to redirect", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public TokenResponse exchangeAuthorizationCodeFromGoogle(String authorizationCode, HttpServletResponse httpServletResponse) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri",
                "http://localhost:8080/public/oauth/callback");
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        HttpEntity<?> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<GoogleTokenResponse> response =
                restTemplate.postForEntity(
                        "https://oauth2.googleapis.com/token",
                        request,
                        GoogleTokenResponse.class
                );

        String idToken = response.getBody().getIdToken();

        return userService
                .createGoogleUser(idToken, httpServletResponse);
    }


    public void githubConsentScreen(HttpServletResponse response) {
        try {
            validateGithubConfig();
            String redirectUri =
                    "http://localhost:8080/public/oauth/github/callback";

            String authUrl =
                    "https://github.com/login/oauth/authorize";

            String url = UriComponentsBuilder
                    .fromHttpUrl(authUrl)
                    .queryParam("client_id", githubClientId)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("scope", "read:user user:email")
                    .build()
                    .toUriString();

            response.sendRedirect(url);
        } catch (Exception e) {
            throw new ApiException("Failed to redirect", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public TokenResponse exchangeAuthorizationCodeFromGithub(String code, HttpServletResponse httpServletResponse) {

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", githubClientId);
        body.add("client_secret", githubClientSecret);
        body.add("code", code);

        body.add("redirect_uri", "http://localhost:8080/public/oauth/github/callback");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<GithubTokenResponse> response =
                restTemplate.postForEntity(
                        "https://github.com/login/oauth/access_token",
                        request,
                        GithubTokenResponse.class
                );

        String accessToken = response.getBody().getAccessToken();

        return userService
                .createGithubUser(accessToken, httpServletResponse);
    }

    public TokenResponse newAccessToken(HttpServletRequest request) {
        return userService.newAccessToken(request);
    }

}
