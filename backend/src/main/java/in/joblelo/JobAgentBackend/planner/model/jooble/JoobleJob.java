package in.joblelo.JobAgentBackend.planner.model.jooble;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoobleJob {

    private String id;

    private String title;

    private String company;

    private String location;

    private String snippet;

    private String salary;

    private String source;

    private String type;

    private String link;

    private String updated;
}