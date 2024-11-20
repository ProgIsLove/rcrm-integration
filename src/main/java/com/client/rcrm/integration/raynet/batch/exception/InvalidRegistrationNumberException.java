package com.client.rcrm.integration.raynet.batch.exception;

public class InvalidRegistrationNumberException extends RuntimeException {

    public InvalidRegistrationNumberException(String message) {
        super(message);
    }

    public InvalidRegistrationNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}
