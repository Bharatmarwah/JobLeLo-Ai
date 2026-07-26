package in.joblelo.JobAgentBackend.service;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyserGenerationService {

    private final OpenAiChatModel analyzerModel;
    private final OpenAiChatModel fallbackAnalyzerModel;

    public String generate(String prompt) {
        try {
            return analyzerModel.chat(prompt);
        } catch (Exception ex) {
            return handleWithFallback(prompt, ex);
        }
    }

    private String handleWithFallback(String prompt, Exception ex) {
        if (!shouldFallback(ex)) {
            throw new RuntimeException("LLM call failed and fallback not applicable", ex);
        }
        log.warn("FallBack model is called", ex);
        try {
            return fallbackAnalyzerModel.chat(prompt);
        } catch (Exception fbEx) {
            log.error("Fallback model also failed", fbEx);
            throw new RuntimeException("Both primary and fallback models failed", fbEx);
        }
    }

    private boolean shouldFallback(Exception ex) {
        String message = ex.getMessage();
        if (message == null) return true;
        message = message.toLowerCase();
        return message.contains("429")
                || message.contains("500")
                || message.contains("502")
                || message.contains("503")
                || message.contains("504")
                || message.contains("timeout")
                || message.contains("deadline")
                || message.contains("unavailable")
                || message.contains("rate limit");
    }

}
