package in.joblelo.JobAgentBackend.repository;
import in.joblelo.JobAgentBackend.entity.UserJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserJobRepo extends JpaRepository<UserJob,Long> {

    List<UserJob> findByUserIdOrderByCreatedAtDesc(String userId);

    List<UserJob> findByUserIdOrderByRelevanceScoreDesc(String userId);
}
