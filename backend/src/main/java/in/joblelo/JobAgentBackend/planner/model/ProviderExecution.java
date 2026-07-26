package in.joblelo.JobAgentBackend.planner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderExecution {

    private boolean executed;

    private boolean success;

    private int jobsFound;

    private String message;
}