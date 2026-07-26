package in.joblelo.JobAgentBackend.planner.response;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import in.joblelo.JobAgentBackend.memory.stm.manager.MemoryManager;
import in.joblelo.JobAgentBackend.model.ChatRequest;
import in.joblelo.JobAgentBackend.model.ChatResponse;
import in.joblelo.JobAgentBackend.model.ResponseType;
import in.joblelo.JobAgentBackend.planner.model.PlannerContext;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResponseGenerator {

    private final ObjectMapper objectMapper;
    private final ResponseCleanerUtil responseCleanerUtil;
    private final MemoryManager memoryManager;
    private final GoogleAiGeminiChatModel responseModel;

    private final ObjectMapper lenientMapper = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
            .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .build();

    public ChatResponse generate(PlannerContext context, ChatRequest chatRequest) {

        try {

            String prompt = buildPrompt(context);

            String rawResponse =
                    responseModel.chat(prompt);

            String cleanedResponse =
                    responseCleanerUtil.cleanJson(rawResponse);

            ChatResponse finalResponse = parseResponse(cleanedResponse);

            finalResponse
                    .setResponseType(ResponseType.SEARCH_JOB);

            if (context.getSearchResultContext() != null
                    && context.getSearchResultContext().getCareerEmails() != null) {
                finalResponse.setCareerEmails(
                        context.getSearchResultContext().getCareerEmails()
                );
            }

            memoryManager
                    .addAssistantMessage(chatRequest.getSessionId(), finalResponse);

            return finalResponse;

        } catch (Exception e) {

            log.error(
                    "Failed to generate final JobLelo response.",
                    e
            );

            return ChatResponse.builder()
                    .responseType(ResponseType.SEARCH_JOB)
                    .response("I found some results, but I couldn't generate the final response. Please try again.")
                    .build();
        }
    }

    private ChatResponse parseResponse(String json) {
        try {
            return objectMapper.readValue(json, ChatResponse.class);
        } catch (Exception e) {
            log.warn("Standard JSON parsing failed for response, trying lenient parser", e);
            return parseLenient(json);
        }
    }

    private ChatResponse parseLenient(String json) {
        try {
            return lenientMapper.readValue(json, ChatResponse.class);
        } catch (Exception e) {
            log.warn("Lenient parse also failed, trying repaired JSON", e);
        }
        try {
            String repaired = responseCleanerUtil.repairJson(json);
            return objectMapper.readValue(repaired, ChatResponse.class);
        } catch (Exception e) {
            log.error("All JSON parsing attempts failed for response generation", e);
            return ChatResponse.builder()
                    .response("I found some results, but I couldn't generate the final response. Please try again.")
                    .build();
        }
    }

    private String buildPrompt(PlannerContext context) {

        try {

            String contextJson =
                    objectMapper.writeValueAsString(context);

            return ResponsePromptProvider.PROMPT
                    .replace("{context}", contextJson);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to build response prompt.",
                    e
            );
        }
    }
}