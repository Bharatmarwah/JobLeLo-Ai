package in.joblelo.JobAgentBackend.planner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlannerResponse {

    // Whether the planner believes the search is complete.
    private boolean goalReached;

    // Internal planner reasoning for logs/debugging.
    private String reason;

    // Actions to execute in this iteration.
    private List<PlannerAction> actions;

}