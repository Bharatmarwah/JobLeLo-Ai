package in.joblelo.JobAgentBackend.planner.model.remotive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemotiveResponse {

    @JsonProperty("job-count")
    private Integer jobCount;

    private List<RemotiveJob> jobs;

}