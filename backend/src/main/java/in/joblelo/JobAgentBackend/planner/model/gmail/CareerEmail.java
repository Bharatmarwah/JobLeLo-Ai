package in.joblelo.JobAgentBackend.planner.model.gmail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerEmail {

    // Gmail message id
    private String messageId;

    // Company / Recruiter
    private String sender;

    // Subject
    private String subject;

    // AI summary (1-2 lines)
    private String summary;

    // What happened
    private CareerEmailType type;

    // Optional company
    private String company;

    // Optional role
    private String role;

    // High / Medium / Low
    private EmailPriority priority;

    // Reply / Confirm Interview / Complete Assessment / View Offer
    private String actionRequired;

    // Meeting / Assessment / Portal link
    private String actionUrl;

    private Instant receivedAt;
}