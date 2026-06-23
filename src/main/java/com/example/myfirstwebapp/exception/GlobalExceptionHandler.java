package com.example.myfirstwebapp.exception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps domain exceptions thrown by the service to HTTP status codes, so the
 * controllers can stay focused on the happy path.
 *
 *   IllegalArgumentException  -> 400 Bad Request   (e.g. invalid amount)
 *   IllegalStateException     -> 409 Conflict       (e.g. insufficient funds)
 *   NoSuchElementException    -> 404 Not Found       (e.g. no such account)
 *
 * Bean Validation failures (@Valid) are already turned into 400 by Spring.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleConflict(IllegalStateException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoSuchElementException ex) {
        return ex.getMessage();
    }
}
