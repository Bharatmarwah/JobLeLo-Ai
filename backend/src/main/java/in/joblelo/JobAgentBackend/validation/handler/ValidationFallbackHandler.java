package in.joblelo.JobAgentBackend.validation.handler;

import in.joblelo.JobAgentBackend.model.ChatResponse;
import in.joblelo.JobAgentBackend.model.ResponseType;
import in.joblelo.JobAgentBackend.validation.model.ValidatorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class ValidationFallbackHandler {

    public ChatResponse fallbackResponse(ValidatorResponse validation) {

        switch (validation.getValidationStatus()) {

            case ERROR -> {
                log.error("Validation error: {}", validation.getReason());

                return ChatResponse.builder()
                        .responseType(ResponseType.UNKNOWN)
                        .response("Sorry, something went wrong while processing your request.")
                        .followUpQuestion("Please try again.")
                        .jobs(Collections.emptyList())
                        .memorySummary("NO_RELEVANT_CONTEXT")
                        .build();
            }

            case REJECTED -> {
                log.info("Validation rejected: {}", validation.getReason());

                return ChatResponse.builder()
                        .responseType(ResponseType.UNKNOWN)
                        .response("""
                                I'm JobLelo, your job search assistant.

                                I can help you find jobs..
                                """)
                        .followUpQuestion("How can I help you today?")
                        .jobs(Collections.emptyList())
                        .memorySummary("NO_RELEVANT_CONTEXT")
                        .build();
            }

            default -> throw new IllegalStateException(
                    "Unexpected validation status: " + validation.getValidationStatus());
        }
    }
}
