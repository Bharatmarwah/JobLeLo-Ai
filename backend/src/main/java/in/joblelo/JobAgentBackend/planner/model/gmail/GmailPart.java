package in.joblelo.JobAgentBackend.planner.model.gmail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmailPart {

    private String mimeType;

    private GmailBody body;

    private List<GmailPart> parts;
}