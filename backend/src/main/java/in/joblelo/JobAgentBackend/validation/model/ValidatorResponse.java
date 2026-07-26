package in.joblelo.JobAgentBackend.validation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorResponse{

    private ValidationStatus validationStatus;

    //Internal reason for logs/debugging
    private String reason;

    // Message to show the user if clarification is needed
    private String userMessage;

    private List<MissingField> missingFields;

    private Double confidence;

    private ValidatedSearchContext validatedSearchContext;

    // Whether planner can continue
    private boolean continueExecution;

}

