package in.joblelo.JobAgentBackend.planner.model.gmail;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedEmail {

    // Gmail message id
    private String messageId;

    // Thread id
    private String threadId;

    // Recruiter / Company email
    private String sender;

    // Recipient
    private String recipient;

    // Email subject
    private String subject;

    // Decoded plain text body
    private String body;

    // Gmail snippet (already plain text)
    private String snippet;

    // Received timestamp
    private Instant receivedAt;
}