package in.joblelo.JobAgentBackend.exceptionhandling;

import org.springframework.http.HttpStatus;

public class ApiException extends JobAgentException {

    public ApiException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public ApiException(String message, HttpStatus status) {
        super(message, status);
    }

    public ApiException(String message, Throwable cause, HttpStatus status) {
        super(message, cause, status);
    }
}

