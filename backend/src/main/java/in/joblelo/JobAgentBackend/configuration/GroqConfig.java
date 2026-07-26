package in.joblelo.JobAgentBackend.configuration;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroqConfig {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.fallback.api.key}")
    private String groqFallbackApiKey;

    @Bean
    public OpenAiChatModel analyzerModel(){
        return OpenAiChatModel.builder()
                .apiKey(groqApiKey)
                .baseUrl("https://api.groq.com/openai/v1")
                .modelName("llama-3.3-70b-versatile")
                .maxRetries(1)
                .temperature(0.0)
                .build();
    }

    @Bean
    public OpenAiChatModel fallbackAnalyzerModel(){
        return OpenAiChatModel.builder()
                .apiKey(groqFallbackApiKey)
                .baseUrl("https://api.groq.com/openai/v1")
                .modelName("openai/gpt-oss-120b")
                .temperature(0.0)
                .maxRetries(1)
                .build();
    }

}
