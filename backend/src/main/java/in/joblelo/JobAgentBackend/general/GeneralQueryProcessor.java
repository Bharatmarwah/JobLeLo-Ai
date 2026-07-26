package in.joblelo.JobAgentBackend.general;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.joblelo.JobAgentBackend.memory.stm.manager.MemoryManager;
import in.joblelo.JobAgentBackend.model.ChatRequest;
import in.joblelo.JobAgentBackend.model.ChatResponse;
import in.joblelo.JobAgentBackend.model.ResponseType;
import in.joblelo.JobAgentBackend.service.ResponseGenerationService;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeneralQueryProcessor {

    private static final String PROMPT = GeneralChatPromptProvider.PROMPT;

    private final ResponseGenerationService responseGenerationService;
    private final MemoryManager memoryManager;
    private final ResponseCleanerUtil responseCleanerUtil;
    private final ObjectMapper objectMapper;

    public ChatResponse process(ChatRequest request, String memory) {

        String prompt = PROMPT
                .replace("{{userQuery}}", request.getMessage())
                .replace("{{memory}}", memory);

        try {

            String rawResponse = responseGenerationService.generate(prompt);

            ChatResponse chatResponse = objectMapper.readValue(
                    responseCleanerUtil.cleanJson(rawResponse),
                    ChatResponse.class
            );

            chatResponse.setResponseType(ResponseType.GENERAL);
            chatResponse.setJobs(Collections.emptyList());

            if (!"NO_RELEVANT_CONTEXT".equals(chatResponse.getMemorySummary())) {
                memoryManager.addAssistantMessage(
                        request.getSessionId(),
                        chatResponse
                );
            }

            return chatResponse;

        } catch (Exception ex) {
            log.error("Failed to generate general response.", ex);
            return buildFallbackResponse();
        }
    }

    private ChatResponse buildFallbackResponse() {
        return ChatResponse.builder()
                .responseType(ResponseType.GENERAL)
                .response("I couldn't complete your request right now. You can try asking about jobs, updating your profile, or searching again.")
                .jobs(Collections.emptyList())
                .followUpQuestion("What would you like to do?")
                .memorySummary("NO_RELEVANT_CONTEXT")
                .build();
    }

}