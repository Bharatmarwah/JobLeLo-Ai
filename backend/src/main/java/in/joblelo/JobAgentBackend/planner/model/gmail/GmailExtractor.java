package in.joblelo.JobAgentBackend.planner.model.gmail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.joblelo.JobAgentBackend.service.AnalyserGenerationService;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static in.joblelo.JobAgentBackend.planner.model.gmail.GmailExtractorPrompt.GMAIL_BATCH_EXTRACTOR_PROMPT;

@Component
@RequiredArgsConstructor
@Slf4j
public class GmailExtractor {

    private final AnalyserGenerationService analyserGenerationService;
    private final ObjectMapper objectMapper;
    private final ResponseCleanerUtil responseCleanerUtil;

    public List<GmailExtractionResult> extractBatch(List<ParsedEmail> emails) {
        String prompt = buildBatchPrompt(emails);

        String raw = analyserGenerationService.generate(prompt);
        if (raw == null) {
            log.warn("[GmailExtractor] All models failed for batch of {} emails", emails.size());
            return List.of();
        }

        try {
            String cleaned = responseCleanerUtil.extractJson(raw);
            return objectMapper.readValue(cleaned, new TypeReference<List<GmailExtractionResult>>() {});
        } catch (Exception e) {
            log.error("[GmailExtractor] Failed to parse batch response", e);
            return List.of();
        }
    }


    private String buildBatchPrompt(List<ParsedEmail> emails) {
        StringBuilder sb = new StringBuilder(GMAIL_BATCH_EXTRACTOR_PROMPT);
        sb.append("\n\n=== EMAILS ===\n");

        for (int i = 0; i < emails.size(); i++) {
            ParsedEmail email = emails.get(i);
            String body = email.getBody();
            if (body != null && body.length() > 3000) {
                body = body.substring(0, 3000);
            }

            sb.append("--- Email ").append(i + 1).append(" ---\n");
            sb.append("Sender: ").append(email.getSender()).append("\n");
            sb.append("Subject: ").append(email.getSubject()).append("\n");
            sb.append("Received: ").append(email.getReceivedAt()).append("\n");
            sb.append("Body:\n").append(body != null ? body : "").append("\n\n");
        }

        return sb.toString();
    }
}
