package com.client.rcrm.integration.raynet.exception;


import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ExceptionResponse> handleRequestNotPermittedException() {
        return ResponseEntity.status(TOO_MANY_REQUESTS)
                .body(
                        ExceptionResponse.builder()
                                .errors(List.of(ExceptionResponse.ExceptionBody.builder()
                                                .title("Rate limit exceed")
                                                .details("You have hit your rate limit of 20 request / hour.")
                                        .build()))
                                .build()
                );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .errors(List.of(ExceptionResponse.ExceptionBody.builder()
                                                .title("Bad Request")
                                                .details(ex.getMessage())
                                        .build()))
                                .build()
                );
    }
}
