package in.joblelo.JobAgentBackend.exceptionhandling;

import org.springframework.http.HttpStatus;

public class MissingAgentInputException extends JobAgentException {

    public MissingAgentInputException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

