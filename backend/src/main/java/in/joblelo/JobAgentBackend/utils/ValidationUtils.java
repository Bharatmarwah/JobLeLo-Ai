package in.joblelo.JobAgentBackend.utils;

import org.springframework.stereotype.Component;

@Component
public class ValidationUtils {

    public void requireNonBlank(String... values) {
        for (String value : values) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Required value is missing.");
            }
        }
    }
}