package edu.java.bot.exceptions;

public class UpdateAlreadyExistsException extends RuntimeException {
    public UpdateAlreadyExistsException(String message) {
        super(message);
    }

    public UpdateAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

