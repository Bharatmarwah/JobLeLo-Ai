package in.joblelo.JobAgentBackend.planner.model.adzuna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdzunaResponse {

    private Integer count;

    private List<AdzunaJob> results;

}