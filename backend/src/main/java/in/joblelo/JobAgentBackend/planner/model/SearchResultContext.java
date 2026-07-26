package in.joblelo.JobAgentBackend.planner.model;

import in.joblelo.JobAgentBackend.planner.model.gmail.CareerEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultContext {

    private Integer totalJobsFound;

    private List<JobMetadata> jobs;

    private List<CareerEmail> careerEmails;
}