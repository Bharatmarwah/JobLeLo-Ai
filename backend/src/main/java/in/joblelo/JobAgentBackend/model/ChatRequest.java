package in.joblelo.JobAgentBackend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {

    @NotBlank
    private String sessionId;
    @NotBlank
    private String message;

}
