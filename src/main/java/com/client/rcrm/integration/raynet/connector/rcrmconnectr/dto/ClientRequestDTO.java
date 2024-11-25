package com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto;

public record ClientRequestDTO(
        String name,
        String rating,
        String state,
        String role,
        String regNumber
) {}
