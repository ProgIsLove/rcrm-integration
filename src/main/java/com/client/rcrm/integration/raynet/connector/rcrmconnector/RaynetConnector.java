package com.client.rcrm.integration.raynet.connector.rcrmconnector;

import com.client.rcrm.integration.raynet.connector.rcrmconnector.dto.ClientRequestDTO;
import com.client.rcrm.integration.raynet.connector.rcrmconnector.dto.ClientResponseDTO;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
@Slf4j
@Setter
public class RaynetConnector {

    private final RestTemplate restTemplate;

    @Value("${raynet.base.url}")
    private String baseUrl;

    @Value("${raynet.instance.name}")
    private String instanceName;

    @Value("${raynet.username}")
    private String username;

    @Value("${raynet.api.key}")
    private String apiKey;


    public RaynetConnector(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        if (username == null || apiKey == null || instanceName == null) {
            throw new RuntimeException("Raynet credentials or instance name are not set correctly.");
        }

        restTemplate.setInterceptors(List.of(new AuthInterceptor(new AuthToken(username, apiKey), instanceName)));
    }


    public ClientResponseDTO fetchCompanyByRegNumber(String regNumber) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("regNumber", regNumber)
                .build()
                .toString();

        try {
            ResponseEntity<ClientResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, ClientResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Unexpected response from API. HTTP Status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error while fetching company: " + e.getMessage(), e);
        } catch (RaynetException e) {
            throw new RaynetException(e.getStatusCode(), e.getTranslatedMessage());
        }
    }

    public Map<String, Object> createCompany(ClientRequestDTO createRequest) {
        String url = baseUrl + "/";

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.PUT, new HttpEntity<>(createRequest),
                    new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Unexpected response from API. HTTP Status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error while creating company: " + e.getMessage(), e);
        } catch (RaynetException e) {
            throw new RaynetException(e.getStatusCode(), e.getTranslatedMessage());
        }
    }

    public Map<String, String> updateCompany(Long companyId, ClientRequestDTO updateRequest) {
        String url = baseUrl + "/" + companyId;

        try {
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(updateRequest),
                    new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Unexpected response from API. HTTP Status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error while updating company: " + e.getMessage(), e);
        } catch (RaynetException e) {
            throw new RaynetException(e.getStatusCode(), e.getTranslatedMessage());
        }
    }
}

