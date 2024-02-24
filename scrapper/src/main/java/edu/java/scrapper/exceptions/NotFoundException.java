package edu.java.scrapper.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String description;

    public NotFoundException(String message, String description) {
        super(message);
        this.description = description;
    }
}
