package in.joblelo.JobAgentBackend.planner.model.gmail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmailMessageResponse {

    private List<GmailMessage> messages;

    private String nextPageToken;

    private Integer resultSizeEstimate;
}