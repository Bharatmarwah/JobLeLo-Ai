package in.joblelo.JobAgentBackend.planner.tools;

import in.joblelo.JobAgentBackend.planner.client.JobClient;
import in.joblelo.JobAgentBackend.planner.mapper.RemotiveMapper;
import in.joblelo.JobAgentBackend.planner.model.JobMetadata;
import in.joblelo.JobAgentBackend.planner.model.ToolResult;
import in.joblelo.JobAgentBackend.planner.model.ToolSchema;
import in.joblelo.JobAgentBackend.planner.model.ToolType;
import in.joblelo.JobAgentBackend.planner.model.remotive.RemotiveResponse;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemotiveTool implements JobSearchTool {

    private final JobClient jobClient;
    private final RemotiveMapper remotiveMapper;

    @Override
    public ToolResult search(ToolSchema input) {

        RemotiveResponse response =
                jobClient.remotiveJobSearch(input);

        if (response == null) {
            log.warn("[RemotiveTool] No response received");
            return ToolResult.builder()
                    .tool(ToolType.REMOTIVE_SEARCH)
                    .success(false)
                    .message("No response received from Remotive.")
                    .jobs(List.of())
                    .careerEmails(List.of())
                    .build();
        }

        List<JobMetadata> jobs =
                ResponseCleanerUtil.limitAndTruncate(
                        remotiveMapper.toJobMetadata(response), 5, 200);

        log.info("[RemotiveTool] Found {} jobs for '{}'",
                jobs.size(), input.getRole());

        return ToolResult.builder()
                .tool(ToolType.REMOTIVE_SEARCH)
                .success(true)
                .message("Jobs fetched successfully from Remotive.")
                .jobs(jobs)
                .build();
    }
}