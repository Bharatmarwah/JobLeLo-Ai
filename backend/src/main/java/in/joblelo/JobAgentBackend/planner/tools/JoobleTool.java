package in.joblelo.JobAgentBackend.planner.tools;

import in.joblelo.JobAgentBackend.planner.client.JobClient;
import in.joblelo.JobAgentBackend.planner.mapper.JoobleMapper;
import in.joblelo.JobAgentBackend.planner.model.JobMetadata;
import in.joblelo.JobAgentBackend.planner.model.ToolResult;
import in.joblelo.JobAgentBackend.planner.model.ToolSchema;
import in.joblelo.JobAgentBackend.planner.model.ToolType;
import in.joblelo.JobAgentBackend.planner.model.jooble.JoobleResponse;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JoobleTool implements JobSearchTool {

    private final JobClient jobClient;
    private final JoobleMapper joobleMapper;

    @Override
    public ToolResult search(ToolSchema input) {

        JoobleResponse response =
                jobClient.joobleJobSearch(input);

        if (response == null) {
            log.warn("[JoobleTool] No response received");
            return ToolResult.builder()
                    .tool(ToolType.JOOBLE_SEARCH)
                    .success(false)
                    .message("No response received from Jooble.")
                    .jobs(List.of())
                    .careerEmails(List.of())
                    .build();
        }

        List<JobMetadata> jobs =
                ResponseCleanerUtil.limitAndTruncate(
                        joobleMapper.toJobMetadata(response), 5, 200);

        log.info("[JoobleTool] Found {} jobs for '{}' in '{}'",
                jobs.size(), input.getRole(), input.getLocation());

        return ToolResult.builder()
                .tool(ToolType.JOOBLE_SEARCH)
                .success(true)
                .message("Jobs fetched successfully from Jooble.")
                .jobs(jobs)
                .build();
    }
}