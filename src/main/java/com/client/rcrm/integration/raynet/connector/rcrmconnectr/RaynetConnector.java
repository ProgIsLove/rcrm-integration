package com.client.rcrm.integration.raynet.connector.rcrmconnectr;

import com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto.CompanyResponseDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@Slf4j
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


    public List<CompanyDTO> getCompanies() {
        String url = baseUrl + "/";

            ResponseEntity<CompanyResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, CompanyResponseDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            CompanyResponseDTO responseDTO = response.getBody();
            if (responseDTO != null && responseDTO.data() != null) {
                return responseDTO.data();
            } else {
                return List.of();
            }
        } else {
            throw new RuntimeException("Failed to fetch companies, status: " + response.getStatusCode());
        }
    }
}
