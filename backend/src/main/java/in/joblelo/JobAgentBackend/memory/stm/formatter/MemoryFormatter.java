package in.joblelo.JobAgentBackend.memory.stm.formatter;

import in.joblelo.JobAgentBackend.memory.stm.model.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemoryFormatter {

    public String formatConversation(List<ChatMessage> messages) {

        if (messages == null || messages.isEmpty()) {
            return "No previous conversation.";
        }

        StringBuilder sb = new StringBuilder();

        for (ChatMessage message : messages) {
            sb.append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append('\n');
        }

        return sb.toString();
    }
}