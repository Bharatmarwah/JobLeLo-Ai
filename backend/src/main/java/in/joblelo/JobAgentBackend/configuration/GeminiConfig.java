package in.joblelo.JobAgentBackend.configuration;

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Bean
    public GoogleAiGeminiChatModel responseModel() {
        return GoogleAiGeminiChatModel
                .builder()
                .apiKey(geminiApiKey)
                .modelName("gemini-2.5-flash")
                .maxRetries(2)
                .temperature(0.5)
                .build();
    }
}
