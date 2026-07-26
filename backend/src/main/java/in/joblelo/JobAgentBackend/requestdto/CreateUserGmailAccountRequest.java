package in.joblelo.JobAgentBackend.requestdto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserGmailAccountRequest {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}
