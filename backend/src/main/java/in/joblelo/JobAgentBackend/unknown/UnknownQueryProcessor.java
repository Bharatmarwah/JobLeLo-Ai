package in.joblelo.JobAgentBackend.unknown;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.joblelo.JobAgentBackend.model.ChatRequest;
import in.joblelo.JobAgentBackend.model.ChatResponse;
import in.joblelo.JobAgentBackend.model.ResponseType;
import in.joblelo.JobAgentBackend.service.ResponseGenerationService;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;


@Component
@RequiredArgsConstructor
public class UnknownQueryProcessor {

    private final static String PROMPT = UnknownChatPromptProvider.PROMPT;
    private final ResponseGenerationService responseGenerationService;
    private final ResponseCleanerUtil responseCleanerUtil;
    private final ObjectMapper objectMapper;

    public ChatResponse process(ChatRequest request,String contextSummary){

        String filledPrompt = PROMPT
                .replace("{{userQuery}}", request.getMessage())
                .replace("{{contextSummary}}",
                        contextSummary);

        try {
            String rawResponse = responseGenerationService.generate(filledPrompt);

            ChatResponse chatResponse = objectMapper.readValue(
                    responseCleanerUtil.cleanJson(rawResponse),
                    ChatResponse.class
            );
            chatResponse.setResponseType(ResponseType.UNKNOWN);
            chatResponse.setJobs(Collections.emptyList());
            chatResponse.setMemorySummary("NO_RELEVANT_CONTEXT");

            return chatResponse;

        } catch (Exception e) {
            return buildFallbackResponse();
        }
    }

    private ChatResponse buildFallbackResponse() {
        return ChatResponse.builder()
                .responseType(ResponseType.UNKNOWN)
                .response("I'm JobLelo, an AI Job Search Agent. I can help you find jobs and provide career guidance.")
                .jobs(Collections.emptyList())
                .followUpQuestion("What kind of job are you looking for?")
                .memorySummary("NO_RELEVANT_CONTEXT")
                .build();
    }


}
