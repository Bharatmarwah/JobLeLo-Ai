package in.joblelo.JobAgentBackend.conversation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationAnalyzerResponse {

    private IntentType intent;

    private double confidence;

    private JobEntities entities;

    private ProfileUpdateOperations profileUpdateOperations;

    private String contextSummary;
}
