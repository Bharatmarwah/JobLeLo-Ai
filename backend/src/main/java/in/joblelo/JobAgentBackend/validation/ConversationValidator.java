package in.joblelo.JobAgentBackend.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.joblelo.JobAgentBackend.model.ChatRequest;
import in.joblelo.JobAgentBackend.service.AnalyserGenerationService;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import in.joblelo.JobAgentBackend.validation.model.ValidatedSearchContext;
import in.joblelo.JobAgentBackend.validation.model.ValidationStatus;
import in.joblelo.JobAgentBackend.validation.model.ValidatorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConversationValidator {

    private final AnalyserGenerationService analyserGenerationService;
    private final ResponseCleanerUtil responseCleanerUtil;
    private final ObjectMapper objectMapper;

    public ValidatorResponse validate(ChatRequest request, String memory) {

        long start = System.currentTimeMillis();

        ValidatorResponse response = pass1(request.getMessage(), memory);

        if (response.getValidationStatus() == ValidationStatus.READY) {
            response.setValidatedSearchContext(
                pass2(request.getMessage(), memory)
            );
        }

        log.debug("Conversation validation completed in {} ms",
                System.currentTimeMillis() - start);

        return response;
    }

    private ValidatorResponse pass1(String userQuery, String memory) {

        String prompt = StatusPromptProvider.PROMPT
                .replace("{{userQuery}}", userQuery)
                .replace("{{memory}}", memory);

        try {

            String rawResponse = analyserGenerationService.generate(prompt);

            ValidatorResponse response = objectMapper.readValue(
                    responseCleanerUtil.cleanJson(rawResponse),
                    ValidatorResponse.class
            );

            response.setValidatedSearchContext(null);

            return response;

        } catch (Exception e) {

            log.error("Status validation failed. userQuery={}", userQuery, e);

            return fallback();
        }
    }

    private ValidatedSearchContext pass2(String userQuery, String memory) {

        String prompt = SearchContextPromptProvider.PROMPT
                .replace("{{userQuery}}", userQuery)
                .replace("{{memory}}", memory);

        try {

            String rawResponse = analyserGenerationService.generate(prompt);

            return objectMapper.readValue(
                    responseCleanerUtil.cleanJson(rawResponse),
                    ValidatedSearchContext.class
            );

        } catch (Exception e) {

            log.error("Context extraction failed for READY query. userQuery={}", userQuery, e);

            return new ValidatedSearchContext();
        }
    }

    private ValidatorResponse fallback() {

        return ValidatorResponse.builder()
                .validationStatus(ValidationStatus.ERROR)
                .validatedSearchContext(null)
                .continueExecution(false)
                .reason("Unable to validate the request due to an internal error.")
                .missingFields(Collections.emptyList())
                .confidence(0.0)
                .build();
    }
}