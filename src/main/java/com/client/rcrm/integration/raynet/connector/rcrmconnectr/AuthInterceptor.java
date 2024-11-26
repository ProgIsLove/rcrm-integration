package com.client.rcrm.integration.raynet.connector.rcrmconnectr;

import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class AuthInterceptor implements ClientHttpRequestInterceptor {

    private final AuthToken authToken;
    private final String instanceName;

    public AuthInterceptor(AuthToken authToken, String instanceName) {
        this.authToken = authToken;
        this.instanceName = instanceName;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().setBasicAuth(authToken.username(), authToken.apiKye());
        request.getHeaders().add("X-Instance-Name", instanceName);
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return execution.execute(request, body);
    }
}
