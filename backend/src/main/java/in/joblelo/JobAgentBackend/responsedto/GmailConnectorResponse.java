package in.joblelo.JobAgentBackend.responsedto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GmailConnectorResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("email")
    private String email;

    @JsonProperty("expires_in")
    private long accessTokenExpiry;


}
