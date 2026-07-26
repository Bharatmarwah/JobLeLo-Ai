package in.joblelo.JobAgentBackend.planner.model.adzuna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdzunaJob {

    private String id;

    private String title;

    private String description;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    private String salary_min;

    private String salary_max;

    private String contract_type;

    private String contract_time;

    private AdzunaCompany company;

    private AdzunaLocation location;
}