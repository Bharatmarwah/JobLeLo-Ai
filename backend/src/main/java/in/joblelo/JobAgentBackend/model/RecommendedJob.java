package in.joblelo.JobAgentBackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedJob {

    private String role;

    private String company;

    private String location;

    private String employmentType;

    private String workplaceType;

    private String salary;

    private String applyUrl;

    private String companyLogo;

    private Double relevanceScore;

    private String recommendationReason;
}