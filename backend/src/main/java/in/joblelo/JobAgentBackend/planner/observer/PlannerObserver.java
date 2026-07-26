package in.joblelo.JobAgentBackend.planner.observer;

import in.joblelo.JobAgentBackend.planner.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PlannerObserver {

    public void observe(
            PlannerContext context,
            PlannerResponse plannerResponse,
            ToolExecutionResult executionResult,
            List<RankedJob> rankedJobs
    ) {

        ExecutionContext executionContext =
                context.getExecutionContext();

        SearchResultContext resultContext =
                context.getSearchResultContext();

        initialize(resultContext);

        // Update execution status of every provider
        for (ToolResult result : executionResult.getResults()) {

            updateProviderExecution(
                    executionContext,
                    result
            );

            if (result.getCareerEmails() != null) {
                resultContext.getCareerEmails()
                        .addAll(result.getCareerEmails());
            }
        }

        // Store ranked jobs as JobMetadata only
        if (rankedJobs != null) {

            for (RankedJob rankedJob : rankedJobs) {

                if (rankedJob.getJob() != null) {
                    resultContext
                            .getJobs()
                            .add(rankedJob.getJob());
                }
            }
        }

        resultContext.setTotalJobsFound(
                resultContext.getJobs().size()
        );

        executionContext.setIteration(
                executionContext.getIteration() + 1
        );

        log.info(
                "Iteration {} completed. Jobs={}, CareerEmails={}",
                executionContext.getIteration(),
                resultContext.getJobs().size(),
                resultContext.getCareerEmails().size()
        );
    }

    private void initialize(SearchResultContext context) {

        if (context.getJobs() == null) {
            context.setJobs(new ArrayList<>());
        }

        if (context.getCareerEmails() == null) {
            context.setCareerEmails(new ArrayList<>());
        }
    }

    private void updateProviderExecution(
            ExecutionContext context,
            ToolResult result
    ) {

        ProviderExecution providerExecution =
                switch (result.getTool()) {

                    case ADZUNA_SEARCH ->
                            context.getAdzuna();

                    case JOOBLE_SEARCH ->
                            context.getJooble();

                    case REMOTIVE_SEARCH ->
                            context.getRemotive();

                    case GMAIL_SEARCH ->
                            context.getGmail();
                };

        providerExecution.setExecuted(true);
        providerExecution.setSuccess(result.isSuccess());

        providerExecution.setJobsFound(
                result.getJobs() == null
                        ? 0
                        : result.getJobs().size()
        );

        providerExecution.setMessage(
                result.getMessage()
        );
    }
}