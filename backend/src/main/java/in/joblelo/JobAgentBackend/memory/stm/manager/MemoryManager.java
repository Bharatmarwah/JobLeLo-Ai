package in.joblelo.JobAgentBackend.memory.stm.manager;

import in.joblelo.JobAgentBackend.memory.stm.StmService;
import in.joblelo.JobAgentBackend.memory.stm.model.ChatMessage;
import in.joblelo.JobAgentBackend.memory.stm.model.MessageRole;
import in.joblelo.JobAgentBackend.model.ChatRequest;
import in.joblelo.JobAgentBackend.model.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemoryManager {

    private final StmService stmService;


    public void addUserMessage(ChatRequest
                                       request) {

        ChatMessage message = new ChatMessage();
        message.setRole(MessageRole.USER);
        message.setContent(request.getMessage());
        message.setCreatedAt(LocalDateTime.now());

        stmService.addMessage(request.getSessionId(), message);

    }

    public void addAssistantMessage(String sessionId, ChatResponse response) {

        ChatMessage message = new ChatMessage();
        message.setRole(MessageRole.ASSISTANT);
        message.setContent(response.getMemorySummary());
        message.setCreatedAt(LocalDateTime.now());

        stmService.addMessage(sessionId, message);

    }

    public List<ChatMessage> getRecentMessages(String sessionId) {
        return stmService.getMessages(sessionId);
    }


}
