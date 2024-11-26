package com.client.rcrm.integration.raynet.connector.rcrmconnector;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class RaynetException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String translatedMessage;

    public RaynetException(HttpStatusCode statusCode, String translatedMessage) {
        this.statusCode = statusCode;
        this.translatedMessage = translatedMessage;
    }
}
