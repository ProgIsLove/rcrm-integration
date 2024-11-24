package com.client.rcrm.integration.raynet.connector.rcrmconnectr;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class RestServiceException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String translatedMessage;

    public RestServiceException(HttpStatusCode statusCode, String translatedMessage) {
        this.statusCode = statusCode;
        this.translatedMessage = translatedMessage;
    }
}
