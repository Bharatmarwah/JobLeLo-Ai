package in.joblelo.JobAgentBackend.memory.ltm.repository;

import in.joblelo.JobAgentBackend.memory.ltm.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepo extends JpaRepository<UserProfile,Long> {
    Optional<UserProfile> findByUserId(String userId);
}
