package in.joblelo.JobAgentBackend.planner.tools;

import in.joblelo.JobAgentBackend.planner.client.GmailClient;
import in.joblelo.JobAgentBackend.planner.manager.GmailTokenManager;
import in.joblelo.JobAgentBackend.planner.model.*;
import in.joblelo.JobAgentBackend.planner.model.gmail.*;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GmailTool implements JobSearchTool {

    private final GmailTokenManager gmailTokenManager;
    private final GmailClient gmailClient;
    private final GmailMessageParser gmailMessageParser;
    private final GmailExtractor gmailExtractor;

    @Override
    public ToolResult search(ToolSchema toolschema) {

        try {
            String accessToken = gmailTokenManager.getAccessToken();
            return executeWithToken(accessToken, toolschema);
        } catch (WebClientResponseException.Unauthorized e) {
            log.warn("Gmail API returned 401, forcing token refresh and retrying...");
            try {
                String freshToken = gmailTokenManager.forceRefresh();
                return executeWithToken(freshToken, toolschema);
            } catch (Exception retryErr) {
                log.error("Gmail search failed even after token refresh", retryErr);
                return ToolResult.builder()
                        .tool(ToolType.GMAIL_SEARCH)
                        .success(false)
                        .message("Gmail search failed: unauthorized. Re-authenticate via Google OAuth to grant gmail.readonly scope.")
                        .jobs(List.of())
                        .build();
            }
        } catch (Exception e) {
            log.error("Gmail search failed", e);
            return ToolResult.builder()
                    .tool(ToolType.GMAIL_SEARCH)
                    .success(false)
                    .message("Gmail search failed: " + e.getMessage())
                    .jobs(List.of())
                    .build();
        }
    }

    private ToolResult executeWithToken(String accessToken, ToolSchema toolschema) {
        GmailMessageResponse messageResponse =
                gmailClient.listMessages(accessToken, toolschema);

        if (messageResponse == null || messageResponse.getMessages() == null) {
            return ToolResult.builder()
                    .tool(ToolType.GMAIL_SEARCH)
                    .success(true)
                    .message("No Gmail messages found.")
                    .jobs(List.of())
                    .careerEmails(List.of())
                    .build();
        }

        List<ParsedEmail> parsedEmails = new ArrayList<>();
        for (GmailMessage message : messageResponse.getMessages()) {
            try {
                GmailMessageDetail detail =
                        gmailClient.getMessage(accessToken, message.getId());
                ParsedEmail parsed = gmailMessageParser.parse(detail);
                parsedEmails.add(parsed);
            } catch (Exception e) {
                log.warn("[GmailTool] Failed to parse message {}: {}", message.getId(), e.getMessage());
            }
        }

        log.info("[GmailTool] Parsed {} Gmail messages, extracting jobs...", parsedEmails.size());

        List<GmailExtractionResult> results = gmailExtractor.extractBatch(parsedEmails);

        List<JobMetadata> jobs = results.stream()
                .filter(r -> r.getJob() != null)
                .map(GmailExtractionResult::getJob)
                .toList();

        List<CareerEmail> careerEmails = results.stream()
                .filter(r -> r.getCareerEmail() != null)
                .map(GmailExtractionResult::getCareerEmail)
                .toList();

        List<JobMetadata> limited = ResponseCleanerUtil.limitAndTruncate(jobs, 5, 200);

        log.info("[GmailTool] Found {} jobs and {} career emails from Gmail batch extraction", limited.size(), careerEmails.size());

        return ToolResult.builder()
                .tool(ToolType.GMAIL_SEARCH)
                .success(true)
                .message("Processed Gmail messages successfully.")
                .jobs(limited)
                .careerEmails(careerEmails)
                .build();
    }
}
