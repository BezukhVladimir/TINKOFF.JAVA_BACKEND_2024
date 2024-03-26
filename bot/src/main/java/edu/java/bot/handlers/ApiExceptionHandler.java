package edu.java.bot.handlers;

import edu.java.bot.api.models.responses.ApiErrorResponse;
import edu.java.bot.exceptions.UpdateAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UpdateAlreadyExistsException.class)
    public ApiErrorResponse handleException(UpdateAlreadyExistsException ex) {
        return new ApiErrorResponse(
            "Update уже существует",
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            getListStringStackTrace(ex)
        );
    }

    private List<String> getListStringStackTrace(Exception exception) {
        List<String> stackTrace = new ArrayList<>();

        for (var stackTraceElement : exception.getStackTrace()) {
            stackTrace.add(stackTraceElement.toString());
        }

        return stackTrace;
    }
}
