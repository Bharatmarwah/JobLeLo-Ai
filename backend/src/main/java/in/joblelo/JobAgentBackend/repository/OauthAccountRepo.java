package in.joblelo.JobAgentBackend.repository;

import in.joblelo.JobAgentBackend.entity.AuthUser;
import in.joblelo.JobAgentBackend.entity.OauthAccount;
import in.joblelo.JobAgentBackend.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthAccountRepo extends JpaRepository<OauthAccount,Long> {
    Optional<OauthAccount> findByUserAndProvider(AuthUser user, Provider provider);
}
