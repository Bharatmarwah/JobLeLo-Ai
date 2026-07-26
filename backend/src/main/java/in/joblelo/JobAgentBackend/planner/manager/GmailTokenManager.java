package in.joblelo.JobAgentBackend.planner.manager;

import in.joblelo.JobAgentBackend.planner.client.GoogleOauthClient;
import in.joblelo.JobAgentBackend.planner.model.gmail.GmailAccessTokenResponse;
import in.joblelo.JobAgentBackend.responsedto.GoogleAccessTokenResponse;
import in.joblelo.JobAgentBackend.service.EncryptionService;
import in.joblelo.JobAgentBackend.service.UserService;
import in.joblelo.JobAgentBackend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class GmailTokenManager {

    private final EncryptionService encryptionService;
    private final UserService userService;
    private final GoogleOauthClient googleOauthClient;
    private final SecurityUtils secretUtil;

    public String getAccessToken() {

        GmailAccessTokenResponse token =
                userService.googleAccessToken(secretUtil.getCurrentUserId());

        Instant expireIn = token.getExpireIn();

        if (expireIn == null || expireIn.isBefore(Instant.now().plusSeconds(60))) {
            return refreshAndSave();
        }

        return token.getAccessToken();
    }

    public String forceRefresh() {
        log.info("Force-refreshing Gmail access token");
        return refreshAndSave();
    }

    private String refreshAndSave() {
        log.info("Refreshing Gmail access token via Google OAuth");

        GoogleAccessTokenResponse response =
               googleOauthClient.refreshAccessToken();

        String encrypted =
                encryptionService.encrypt(response.getAccessToken());

        Instant expiry =
                Instant.now().plusSeconds(response.getExpiresIn());

        userService.updateGmailAccount(encrypted, expiry);

        log.info("Gmail access token refreshed, scope: {}", response.getScope());

        return response.getAccessToken();
    }

}
