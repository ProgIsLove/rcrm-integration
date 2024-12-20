package com.client.rcrm.integration.raynet.connector.config;

import com.client.rcrm.integration.raynet.connector.rcrmconnector.CustomErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new CustomErrorHandler());
        return restTemplate;
    }
}
