package in.joblelo.JobAgentBackend.planner.model.gmail;

import in.joblelo.JobAgentBackend.planner.model.JobMetadata;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GmailExtractionResult {

    private JobMetadata job;

    private CareerEmail careerEmail;
}