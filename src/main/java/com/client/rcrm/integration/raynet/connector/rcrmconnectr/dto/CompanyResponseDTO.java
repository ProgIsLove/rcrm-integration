package com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto;

import java.util.List;

public record CompanyResponseDTO(boolean success,
                                 int totalCount,
                                 List<CompanyDTO> data) {
}
