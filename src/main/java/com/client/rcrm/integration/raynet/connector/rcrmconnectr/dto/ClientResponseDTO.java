package com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto;

import java.util.List;

public record ClientResponseDTO(boolean success,
                                int totalCount,
                                List<ClientDTO> data) {
}
