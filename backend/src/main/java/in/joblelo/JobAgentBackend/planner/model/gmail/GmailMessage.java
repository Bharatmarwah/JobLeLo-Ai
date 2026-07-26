package in.joblelo.JobAgentBackend.planner.model.gmail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmailMessage {

    private String id;

    private String threadId;
}