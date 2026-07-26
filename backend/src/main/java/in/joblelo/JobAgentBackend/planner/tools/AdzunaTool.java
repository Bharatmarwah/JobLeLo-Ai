package in.joblelo.JobAgentBackend.planner.tools;

import in.joblelo.JobAgentBackend.planner.client.JobClient;
import in.joblelo.JobAgentBackend.planner.mapper.AdzunaMapper;
import in.joblelo.JobAgentBackend.planner.model.JobMetadata;
import in.joblelo.JobAgentBackend.planner.model.ToolResult;
import in.joblelo.JobAgentBackend.planner.model.ToolSchema;
import in.joblelo.JobAgentBackend.planner.model.ToolType;
import in.joblelo.JobAgentBackend.planner.model.adzuna.AdzunaResponse;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdzunaTool implements JobSearchTool {

    private final JobClient jobClient;
    private final AdzunaMapper adzunaMapper;

    @Override
    public ToolResult search(ToolSchema input) {

        AdzunaResponse response =
                jobClient.adzunaJobSearch(input);

        if (response == null) {
            log.warn("[AdzunaTool] No response received");
            return ToolResult.builder()
                    .tool(ToolType.ADZUNA_SEARCH)
                    .success(false)
                    .message("No response received from Adzuna.")
                    .jobs(List.of())
                    .careerEmails(List.of())
                    .build();
        }

        List<JobMetadata> jobs =
                ResponseCleanerUtil.limitAndTruncate(
                        adzunaMapper.toJobMetadata(response), 10, 200);

        log.info("[AdzunaTool] Found {} jobs for '{}' in '{}'",
                jobs.size(), input.getRole(), input.getLocation());

        return ToolResult.builder()
                .tool(ToolType.ADZUNA_SEARCH)
                .success(true)
                .message("Fetched jobs from Adzuna successfully.")
                .jobs(jobs)
                .build();
    }
}
