package edu.java.bot.exceptions;

import edu.java.bot.api.models.responses.ApiErrorResponse;
import lombok.Getter;


@Getter
public class ApiErrorException extends RuntimeException {
    private final ApiErrorResponse errorResponse;

    public ApiErrorException(ApiErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}
