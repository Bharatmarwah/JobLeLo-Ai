package in.joblelo.JobAgentBackend.planner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionContext {

    private boolean goalReached;

    private int iteration;

    private String currentRole;

    @Builder.Default
    private List<String> attemptedRoles = new ArrayList<>();

    @Builder.Default
    private ProviderExecution adzuna = ProviderExecution.builder().executed(false).success(false).jobsFound(0).message("").build();

    @Builder.Default
    private ProviderExecution jooble = ProviderExecution.builder().executed(false).success(false).jobsFound(0).message("").build();

    @Builder.Default
    private ProviderExecution remotive = ProviderExecution.builder().executed(false).success(false).jobsFound(0).message("").build();

    @Builder.Default
    private ProviderExecution gmail = ProviderExecution.builder().executed(false).success(false).jobsFound(0).message("").build();
}