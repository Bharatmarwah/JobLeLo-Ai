package in.joblelo.JobAgentBackend.planner.model.gmail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmailPayload {

    private String mimeType;

    private GmailBody body;

    private List<GmailHeader> headers;

    private List<GmailPart> parts;
}