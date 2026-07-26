package in.joblelo.JobAgentBackend.planner.executor;

import in.joblelo.JobAgentBackend.planner.tools.AdzunaTool;
import in.joblelo.JobAgentBackend.planner.tools.GmailTool;
import in.joblelo.JobAgentBackend.planner.tools.JoobleTool;
import in.joblelo.JobAgentBackend.planner.tools.RemotiveTool;
import in.joblelo.JobAgentBackend.planner.model.PlannerAction;
import in.joblelo.JobAgentBackend.planner.model.PlannerResponse;
import in.joblelo.JobAgentBackend.planner.model.ToolExecutionResult;
import in.joblelo.JobAgentBackend.planner.model.ToolResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class ToolExecutor {
    private final AdzunaTool adzunaTool;
    private final JoobleTool joobleTool;
    private final RemotiveTool remotiveTool;
    private final GmailTool gmailTool;
    private final Executor executor;

    public ToolExecutor(
            AdzunaTool adzunaTool,
            JoobleTool joobleTool,
            RemotiveTool remotiveTool,
            GmailTool gmailTool,
            @Qualifier("jobToolExecutor") Executor executor) {

        this.adzunaTool = adzunaTool;
        this.joobleTool = joobleTool;
        this.remotiveTool = remotiveTool;
        this.gmailTool = gmailTool;
        this.executor = executor;
    }

    public ToolExecutionResult execute(PlannerResponse plannerResponse) {

        if (plannerResponse.getActions() == null ||
                plannerResponse.getActions().isEmpty()) {
            log.info("[ToolExecutor] No actions to execute");
            return ToolExecutionResult.builder()
                    .results(List.of())
                    .build();
        }

        log.info("[ToolExecutor] Executing {} actions for {}",
                plannerResponse.getActions().size(), plannerResponse.getReason());

        List<CompletableFuture<ToolResult>> futures =
                plannerResponse.getActions()
                        .stream()
                        .map(action ->
                                CompletableFuture.supplyAsync(
                                        () -> executeTool(action),
                                        executor
                                )
                        )
                        .toList();

        List<ToolResult> results =
                futures.stream()
                        .map(CompletableFuture::join)
                        .toList();

        long successCount = results.stream().filter(ToolResult::isSuccess).count();
        long totalJobs = results.stream()
                .filter(r -> r.getJobs() != null)
                .mapToLong(r -> r.getJobs().size())
                .sum();

        log.info("[ToolExecutor] Completed: {}/{} succeeded, {} total jobs",
                successCount, results.size(), totalJobs);

        return ToolExecutionResult.builder()
                .results(results)
                .build();
    }

    private ToolResult executeTool(PlannerAction action) {

        try {

            return switch (action.getTool()) {

                case ADZUNA_SEARCH -> adzunaTool.search(action.getInput());

                case JOOBLE_SEARCH -> joobleTool.search(action.getInput());

                case REMOTIVE_SEARCH -> remotiveTool.search(action.getInput());

                case GMAIL_SEARCH -> gmailTool.search(action.getInput());
            };

        } catch (Exception e) {

            log.error("Tool {} failed", action.getTool(), e);

            return ToolResult.builder()
                    .tool(action.getTool())
                    .success(false)
                    .message(e.getMessage())
                    .jobs(List.of())
                    .careerEmails(List.of())
                    .build();
        }
    }
}