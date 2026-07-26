package in.joblelo.JobAgentBackend.entity;

import in.joblelo.JobAgentBackend.planner.model.JobProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "user_jobs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_provider_job",
                        columnNames = {"userId", "provider", "providerJobId"}
                )
        }
)
public class UserJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobProvider provider;

    @Column(nullable = false)
    private String providerJobId;

    private String role;
    private String company;
    private String location;
    private String employmentType;
    private Integer experience;
    private String salary;

    @Column(length = 2000)
    private String applyUrl;

    @Column(length = 5000)
    private String description;

    private Double relevanceScore;

    @Column(length = 1000)
    private String rankingReason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

}
