package in.joblelo.JobAgentBackend.validation.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.joblelo.JobAgentBackend.memory.stm.manager.MemoryManager;
import in.joblelo.JobAgentBackend.model.ChatRequest;
import in.joblelo.JobAgentBackend.model.ChatResponse;
import in.joblelo.JobAgentBackend.model.ResponseType;
import in.joblelo.JobAgentBackend.service.ResponseGenerationService;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import in.joblelo.JobAgentBackend.validation.NeedInformationPromptProvider;
import in.joblelo.JobAgentBackend.validation.model.ValidatorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationClassificationHandler {

    private final static String PROMPT = NeedInformationPromptProvider.PROMPT;
    private final ResponseCleanerUtil responseCleanerUtil;
    private final ObjectMapper objectMapper;
    private final MemoryManager memoryManager;
    private final ResponseGenerationService responseGenerationService;


    public ChatResponse classificationResponse(ChatRequest request, ValidatorResponse validatorResponse) {
        log.info("Validation classification for information needed to search job. userMessage={}",
                validatorResponse.getUserMessage());

        String userMessage = validatorResponse.getUserMessage();

        if (userMessage==null||userMessage.isBlank()) {
            userMessage = "Could you tell me which job role and location you're interested in?";
        }
        try {
            String filledUp = PROMPT
                    .replace("{{userMessage}}", userMessage);

            String rawResponse = responseGenerationService
                    .generate(filledUp);

            String response = responseCleanerUtil
                    .cleanJson(rawResponse);

            ChatResponse chatResponse = objectMapper
                    .readValue
                            (response, ChatResponse.class);

            chatResponse.setResponseType(ResponseType.SEARCH_JOB);
            chatResponse.setJobs(Collections.emptyList());

            memoryManager
                    .addAssistantMessage(request.getSessionId(), chatResponse);

            return chatResponse;

        }catch (Exception e){
            log.error("Failed to generate information needed classification response. userMessage={}", userMessage, e);
            return fallback(userMessage);
        }
    }
    private ChatResponse fallback(String userMessage) {

        return ChatResponse.builder()
                .responseType(ResponseType.SEARCH_JOB)
                .response(userMessage)
                .jobs(Collections.emptyList())
                .followUpQuestion(null)
                .memorySummary("NO_RELEVANT_CONTEXT")
                .build();
    }
}
