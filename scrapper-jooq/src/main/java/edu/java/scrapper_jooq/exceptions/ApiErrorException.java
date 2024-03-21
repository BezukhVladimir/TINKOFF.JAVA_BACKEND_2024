package edu.java.scrapper_jooq.exceptions;

import edu.java.scrapper_jooq.api.models.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ApiErrorException extends RuntimeException {
    private final ApiErrorResponse errorResponse;

    public ApiErrorException(ApiErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}
