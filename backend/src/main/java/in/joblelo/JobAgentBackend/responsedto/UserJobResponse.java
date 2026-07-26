package in.joblelo.JobAgentBackend.responsedto;

import in.joblelo.JobAgentBackend.planner.model.JobProvider;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserJobResponse {
    private Long id;

    private JobProvider provider;

    private String providerJobId;

    private String role;
    private String company;
    private String location;
    private String employmentType;
    private Integer experience;
    private String salary;

    private String applyUrl;

    private String description;

    private Double relevanceScore;

    private String rankingReason;

    private Instant createdAt;
}
