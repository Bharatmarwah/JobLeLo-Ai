package in.joblelo.JobAgentBackend.planner.model.gmail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmailMessageDetail {

    private String id;

    private String threadId;

    private String snippet;

    private String internalDate;

    private GmailPayload payload;
}