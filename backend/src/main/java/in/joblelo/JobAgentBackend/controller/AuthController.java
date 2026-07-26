package in.joblelo.JobAgentBackend.controller;

import in.joblelo.JobAgentBackend.responsedto.TokenResponse;
import in.joblelo.JobAgentBackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
public class AuthController {

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    private final AuthService authService;

    @GetMapping("/google/login")
    public void loginWithGoogle(HttpServletResponse response) {
        authService.googleConsentScreen(response);
    }

    @GetMapping("/oauth/callback")
    public void callbackForGoogle(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Authorization code is required");
        }
        TokenResponse token = authService.exchangeAuthorizationCodeFromGoogle(code, response);
        response.sendRedirect(frontendUrl + "/login?token=" + token.getAccessToken());
    }

    @GetMapping("/github/login")
    public void loginWithGithub(HttpServletResponse response) {
        authService.githubConsentScreen(response);
    }

    @GetMapping("/oauth/github/callback")
    public void callbackForGithub(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Authorization code is required");
        }
        TokenResponse token = authService.exchangeAuthorizationCodeFromGithub(code, response);
        response.sendRedirect(frontendUrl + "/login?token=" + token.getAccessToken());
    }

    @PostMapping("/refreshtoken")
    public TokenResponse refreshToken(HttpServletRequest httpServletRequest) {
        return authService.newAccessToken(httpServletRequest);
    }
}
