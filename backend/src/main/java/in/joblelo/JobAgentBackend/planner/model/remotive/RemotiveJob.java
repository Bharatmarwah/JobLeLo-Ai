package in.joblelo.JobAgentBackend.planner.model.remotive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemotiveJob {

    private Long id;

    private String url;

    private String title;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_logo")
    private String companyLogo;

    private String category;

    @JsonProperty("job_type")
    private String jobType;

    @JsonProperty("publication_date")
    private String publicationDate;

    @JsonProperty("candidate_required_location")
    private String candidateRequiredLocation;

    private String salary;

    private String description;
}