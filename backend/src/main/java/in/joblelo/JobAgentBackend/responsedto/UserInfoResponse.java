package in.joblelo.JobAgentBackend.responsedto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoResponse {

    private String username;
    private String email;
    private String profileUrl;
    private Instant createdAt;



}
