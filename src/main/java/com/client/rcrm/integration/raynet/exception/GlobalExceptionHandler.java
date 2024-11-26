package com.client.rcrm.integration.raynet.exception;


import com.client.rcrm.integration.raynet.connector.rcrmconnectr.RestServiceException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${resilience4j.ratelimiter.instances.apiUploadDataRateLimit.limitForPeriod}")
    private int rateLimit;

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ExceptionResponse> handleRequestNotPermittedException() {
        return ResponseEntity.status(TOO_MANY_REQUESTS)
                .body(
                        ExceptionResponse.builder()
                                .errors(List.of(ExceptionResponse.ExceptionBody.builder()
                                        .title("Rate limit exceed")
                                        .details("""
                                                You have hit your rate limit of %d requests.
                                                Please try again after some time.
                                                """.formatted(rateLimit))
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

    @ExceptionHandler(RestServiceException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RestServiceException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .errors(List.of(ExceptionResponse.ExceptionBody.builder()
                                        .title(ex.getStatusCode().toString())
                                        .details(ex.getTranslatedMessage())
                                        .build()))
                                .build()
                );
    }
}
