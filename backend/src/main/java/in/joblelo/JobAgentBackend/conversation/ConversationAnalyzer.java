package in.joblelo.JobAgentBackend.conversation;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.joblelo.JobAgentBackend.conversation.model.ConversationAnalyzerResponse;
import in.joblelo.JobAgentBackend.conversation.model.JobEntities;
import in.joblelo.JobAgentBackend.conversation.model.IntentType;
import in.joblelo.JobAgentBackend.model.ChatRequest;
import in.joblelo.JobAgentBackend.service.AnalyserGenerationService;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConversationAnalyzer {

    private final AnalyserGenerationService analyserGenerationService;
    private final ObjectMapper objectMapper;
    private final static String PROMPT= AnalyzerPromptProvider.PROMPT;
    private final ResponseCleanerUtil responseCleanerUtil;

    public ConversationAnalyzerResponse classifyWithContext(
            ChatRequest request,
            String previousConversations){

        String filledPrompt = PROMPT
                .replace("{userQuery}",request.getMessage())
                .replace("{conversationHistory}",previousConversations);

        try{
            String rawResponse = analyserGenerationService.generate(filledPrompt);
            String response = responseCleanerUtil.cleanJson(rawResponse);
            return objectMapper.readValue(response,ConversationAnalyzerResponse.class);

        }catch (Exception e){
            log.error("Failed to process ConversationAnalyzerResponse for userQuery: {}",
                    request.getMessage(),
                    e);
            return fallback();
        }
    }

    private static ConversationAnalyzerResponse fallback(){
        ConversationAnalyzerResponse response = new ConversationAnalyzerResponse();
        response.setIntent(IntentType.UNKNOWN);
        response.setConfidence(0.01);
        response.setEntities(new JobEntities());
        response.setContextSummary(null);
        return response;
    }
}

