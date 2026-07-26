package in.joblelo.JobAgentBackend.responsedto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {

    @NotBlank
    private String accessToken;
    @NotBlank
    private String tokenType;


}

