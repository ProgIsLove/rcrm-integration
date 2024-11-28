package com.client.rcrm.integration.raynet;

import com.client.rcrm.integration.raynet.connector.rcrmconnector.dto.ClientResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class RaynetIntegrationTest {

    @Value("${raynet.base.url}")
    private String baseUrl;

    @Value("${raynet.instance.name}")
    private String instanceName;

    @Value("${raynet.username}")
    private String username;

    @Value("${raynet.api.key}")
    private String apiKey;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void fetchCompanyByRegNumber_shouldReturnClientResponseDTO_whenResponseIsOk() {
        String regNumber = "27767680";
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("regNumber", regNumber)
                .build().toUriString();

        String authValue = username + ":" + apiKey;
        String encodedAuth = "Basic " + new String(java.util.Base64.getEncoder().encode(authValue.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encodedAuth);
        headers.set("X-instance-Name", instanceName);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ClientResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, ClientResponseDTO.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ClientResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.data().size());
    }

    @Test
    void fetchCompanyByRegNumber_shouldReturnEmptyResponse_whenResponseIsNotExist() {
        // Given
        String regNumber = "12345";
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("regNumber", regNumber)
                .build().toUriString();

        String authValue = username + ":" + apiKey;
        String encodedAuth = "Basic " + new String(java.util.Base64.getEncoder().encode(authValue.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encodedAuth);
        headers.set("X-instance-Name", instanceName);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ClientResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, ClientResponseDTO.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ClientResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.success());
        assertTrue(responseBody.data().isEmpty());
    }

    @Test
    void fetchCompanyByRegNumber_shouldThrowRuntimeException_onHttpClientError() {
        String regNumber = "nonexistent";
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("nonexistent", regNumber)
                .build().toUriString();

        String authValue = username + ":" + apiKey;
        String encodedAuth = "Basic " + new String(java.util.Base64.getEncoder().encode(authValue.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encodedAuth);
        headers.set("X-instance-Name", instanceName);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ClientResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, ClientResponseDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
