package in.joblelo.JobAgentBackend.planner.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PlannerContext {

    private SearchContext searchContext;

    private ExecutionContext executionContext;

    private SearchResultContext searchResultContext;
}