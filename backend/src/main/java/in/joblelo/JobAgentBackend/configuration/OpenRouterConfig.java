package in.joblelo.JobAgentBackend.configuration;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenRouterConfig {

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Bean
    public OpenAiChatModel fallbackResponseModel(){
        return OpenAiChatModel.builder()
                .apiKey(openRouterApiKey)
                .baseUrl("https://openrouter.ai/api/v1")
                .modelName("qwen/qwen3-235b-a22b:free")
                .temperature(0.5)
                .build();
    }

}
