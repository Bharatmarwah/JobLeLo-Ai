package in.joblelo.JobAgentBackend.planner.model.gmail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GmailAccessTokenResponse {
    private String accessToken;
    private Instant expireIn;
}
