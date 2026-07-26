package in.joblelo.JobAgentBackend.repository;

import in.joblelo.JobAgentBackend.entity.AuthUser;
import in.joblelo.JobAgentBackend.entity.GmailAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GmailAccountRepo extends JpaRepository<GmailAccount,Long> {
    Optional<GmailAccount> findByUser(AuthUser user);
}
