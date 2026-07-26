package in.joblelo.JobAgentBackend.model;


import in.joblelo.JobAgentBackend.planner.model.gmail.CareerEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private ResponseType responseType;

    private String response;

    @Builder.Default
    private List<RecommendedJob> jobs = new ArrayList<>();

    @Builder.Default
    private List<CareerEmail> careerEmails = new ArrayList<>();

    private String followUpQuestion;

    // Internal only
    private String memorySummary;
}