package in.joblelo.JobAgentBackend.planner;

import in.joblelo.JobAgentBackend.model.ChatRequest;
import in.joblelo.JobAgentBackend.model.ChatResponse;
import in.joblelo.JobAgentBackend.planner.executor.ToolExecutor;
import in.joblelo.JobAgentBackend.planner.model.*;
import in.joblelo.JobAgentBackend.planner.observer.PlannerObserver;
import in.joblelo.JobAgentBackend.planner.ranker.DuplicateJobService;
import in.joblelo.JobAgentBackend.planner.ranker.JobRanker;
import in.joblelo.JobAgentBackend.planner.response.ResponseGenerator;
import in.joblelo.JobAgentBackend.service.UserJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobSearchOrchestrator {

    private static final int JOB_THRESHOLD = 5;

    private final ToolExecutor toolExecutor;
    private final PlannerObserver plannerObserver;
    private final JobRanker jobRanker;
    private final DuplicateJobService duplicateJobService;
    private final UserJobService userJobService;
    private final ResponseGenerator responseGenerator;

    public ChatResponse plan(ChatRequest request, PlannerContext plannerContext) {

        SearchContext searchContext = plannerContext.getSearchContext();
        List<JobMetadata> allJobs = new ArrayList<>();
        List<ToolResult> allToolResults = new ArrayList<>();
        int phasesExecuted = 0;

        ToolSchema[] phases = {
                phase1(searchContext),
                phase2(searchContext),
                phase3(searchContext)
        };

        for (int i = 0; i < phases.length; i++) {
            phasesExecuted++;

            boolean isFirst = (i == 0);
            boolean isLast = (i == phases.length - 1);

            List<PlannerAction> actions = buildActions(phases[i], isFirst);

            PlannerResponse plannerResponse = PlannerResponse.builder()
                    .goalReached(isLast)
                    .reason("Phase " + (i + 1))
                    .actions(actions)
                    .build();

            ToolExecutionResult result = toolExecutor.execute(plannerResponse);
            allToolResults.addAll(result.getResults());

            List<JobMetadata> phaseJobs = result.getResults().stream()
                    .filter(r -> r.getJobs() != null)
                    .flatMap(r -> r.getJobs().stream())
                    .toList();
            allJobs.addAll(phaseJobs);

            long uniqueCount = allJobs.stream()
                    .map(j -> j.getProvider() + "|" + j.getJobId())
                    .distinct()
                    .count();

            if (uniqueCount >= JOB_THRESHOLD || isLast) {
                plannerContext.getExecutionContext().setGoalReached(true);
                break;
            }
        }

        List<RankedJob> rankedJobs =
                jobRanker.rank(searchContext, allJobs);

        if (rankedJobs.isEmpty() && !allJobs.isEmpty()) {
            log.warn("Ranking returned 0 jobs (likely LLM failure), falling back to raw collection ({} jobs)", allJobs.size());
            rankedJobs = allJobs.stream()
                    .limit(15)
                    .map(j -> RankedJob.builder()
                            .job(j)
                            .relevanceScore(0.5)
                            .recommendationReason("")
                            .build())
                    .toList();
        }

        rankedJobs =
                duplicateJobService.removeDuplicates(rankedJobs, plannerContext);

        List<RankedJob> beforeFilter = rankedJobs;
        rankedJobs = rankedJobs.stream()
                .filter(j -> j.getRelevanceScore() >= 0.5)
                .toList();
        int filtered = beforeFilter.size() - rankedJobs.size();
        if (filtered > 0) {
            log.info("Filtered out {} jobs with relevanceScore < 0.5", filtered);
        }

        userJobService.save(rankedJobs);

        plannerContext.getExecutionContext().setIteration(phasesExecuted - 1);

        plannerObserver.observe(
                plannerContext,
                null,
                ToolExecutionResult.builder().results(allToolResults).build(),
                rankedJobs
        );

        return responseGenerator.generate(plannerContext, request);
    }

    private static List<PlannerAction> buildActions(ToolSchema schema, boolean includeGmail) {
        List<PlannerAction> actions = new ArrayList<>(List.of(
                PlannerAction.builder().tool(ToolType.ADZUNA_SEARCH).input(schema).reason("").build(),
                PlannerAction.builder().tool(ToolType.JOOBLE_SEARCH).input(schema).reason("").build(),
                PlannerAction.builder().tool(ToolType.REMOTIVE_SEARCH).input(schema).reason("").build()
        ));
        if (includeGmail) {
            actions.add(
                    PlannerAction.builder().tool(ToolType.GMAIL_SEARCH).input(schema).reason("").build()
            );
        }
        return actions;
    }

    private static ToolSchema phase1(SearchContext searchContext) {
        ToolSchema input = new ToolSchema();
        input.setRole(searchContext.getQueryRole());
        input.setLocation(searchContext.getLocation());
        input.setExperienceType(searchContext.getEmployeeType());
        input.setExperience(searchContext.getExperience());
        input.setSkills(searchContext.getSkills());

        return input;
    }

    private static ToolSchema phase2(SearchContext searchContext) {
        ToolSchema input = new ToolSchema();
        input.setRole(searchContext.getProfileRole());
        input.setLocation(searchContext.getLocation());
        input.setExperienceType(searchContext.getEmployeeType());
        input.setExperience(searchContext.getExperience());
        input.setSkills(searchContext.getSkills());

        return input;
    }

    private static ToolSchema phase3(SearchContext searchContext) {
        ToolSchema input = new ToolSchema();
        input.setRole(searchContext.getQueryRole());
        input.setExperienceType(searchContext.getEmployeeType());
        input.setExperience(searchContext.getExperience());
        input.setSkills(searchContext.getSkills());

        return input;
    }

}
