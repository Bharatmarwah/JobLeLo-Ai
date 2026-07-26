package in.joblelo.JobAgentBackend.planner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankedJob {

    private JobMetadata job;

    private double relevanceScore;

    private String rankingReason;

    private String recommendationReason;

}