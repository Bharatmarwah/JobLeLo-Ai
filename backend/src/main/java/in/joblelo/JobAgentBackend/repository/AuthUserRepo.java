package in.joblelo.JobAgentBackend.repository;

import in.joblelo.JobAgentBackend.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepo extends JpaRepository<AuthUser,String> {
    Optional<AuthUser> findByEmail(String email);
}
