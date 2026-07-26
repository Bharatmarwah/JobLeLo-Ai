package in.joblelo.JobAgentBackend.exceptionhandling;

import org.springframework.http.HttpStatus;

public class JobAgentException extends RuntimeException {

    private final HttpStatus status;

    public JobAgentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public JobAgentException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

