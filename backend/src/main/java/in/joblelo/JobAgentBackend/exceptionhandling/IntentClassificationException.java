package in.joblelo.JobAgentBackend.exceptionhandling;

import org.springframework.http.HttpStatus;

public class IntentClassificationException extends JobAgentException {

    public IntentClassificationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_GATEWAY);
    }

    public IntentClassificationException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}

