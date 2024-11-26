package com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RestTemplateError(
        @JsonProperty("status")
        String status,
        @JsonProperty("translatedMessage")
        String translatedMessage) {
}
