package in.joblelo.JobAgentBackend.utils;

import in.joblelo.JobAgentBackend.entity.AuthUser;
import in.joblelo.JobAgentBackend.entity.GmailAccount;
import in.joblelo.JobAgentBackend.exceptionhandling.ApiException;
import in.joblelo.JobAgentBackend.repository.AuthUserRepo;
import in.joblelo.JobAgentBackend.repository.GmailAccountRepo;
import in.joblelo.JobAgentBackend.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenUtil {

    private final SecurityUtils securityUtils;
    private final GmailAccountRepo gmailAccountRepo;
    private final AuthUserRepo authUserRepo;
    private final EncryptionService encryptionService;

    public String getRefreshToken() {

        String userId = securityUtils.getCurrentUserId();

        AuthUser user = authUserRepo.findById(userId)
                .orElseThrow(() ->
                        new ApiException("User not found", HttpStatus.NOT_FOUND));

        GmailAccount gmailAccount = gmailAccountRepo.findByUser(user)
                .orElseThrow(() ->
                        new ApiException("Gmail account not found", HttpStatus.NOT_FOUND));

        return encryptionService.decrypt(
                gmailAccount.getEncryptedRefreshToken()
        );
    }
}