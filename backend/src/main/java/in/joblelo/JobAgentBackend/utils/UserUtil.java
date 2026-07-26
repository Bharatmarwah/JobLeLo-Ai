package in.joblelo.JobAgentBackend.utils;

import in.joblelo.JobAgentBackend.entity.AuthUser;
import in.joblelo.JobAgentBackend.repository.AuthUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private final AuthUserRepo authUserRepo;

    public boolean isValidUserId(String userId){

        Optional<AuthUser> user = authUserRepo.findById(userId);

        if(user.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

}
