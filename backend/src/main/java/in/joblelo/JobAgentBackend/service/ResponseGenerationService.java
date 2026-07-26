package in.joblelo.JobAgentBackend.service;

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponseGenerationService {

    private final GoogleAiGeminiChatModel responseModel;
    private final OpenAiChatModel fallbackResponseModel;

    public String generate(String prompt) {

        try {
            return responseModel.chat(prompt);

        } catch (Exception ex) {

            if (!shouldFallback(ex)) {
                throw ex;
            }

            log.warn("Gemini unavailable. Falling back to OpenRouter.", ex);

            return fallbackResponseModel.chat(prompt);
        }
    }

    private boolean shouldFallback(Exception ex) {

        String message = ex.getMessage();

        if (message == null) {
            return true;
        }

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