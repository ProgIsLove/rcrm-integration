package com.client.rcrm.integration.raynet.connector.rcrmconnectr;

import com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto.RestTemplateError;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class CustomErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public CustomErrorHandler() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.is4xxClientError() || statusCode.is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();

        if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
            String httpBodyResponse = readResponseBody(response);

            RestTemplateError restTemplateError = parseErrorResponse(httpBodyResponse);

            throw new RestServiceException(
                    statusCode,
                    restTemplateError.translatedMessage()
            );
        }
    }

    private String readResponseBody(ClientHttpResponse response) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private RestTemplateError parseErrorResponse(String responseBody) throws IOException {
        return objectMapper.readValue(responseBody, RestTemplateError.class);
    }
}
