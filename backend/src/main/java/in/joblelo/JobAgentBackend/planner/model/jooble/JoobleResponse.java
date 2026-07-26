package in.joblelo.JobAgentBackend.planner.model.jooble;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoobleResponse {

    private Integer totalCount;

    private List<JoobleJob> jobs;

}