package in.joblelo.JobAgentBackend.planner.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlannerAction {

    private ToolType tool;

    // Tool specific schema
    private ToolSchema input;

    // Why this tool was selected
    private String reason;

}