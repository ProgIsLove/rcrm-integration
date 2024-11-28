package com.client.rcrm.integration.raynet.connector.rcrmconnector.mapper;



import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import com.client.rcrm.integration.raynet.connector.rcrmconnector.dto.ClientRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class ReynetMapper {

    public ClientRequestDTO mapToClientRequestDTO(Company company) {
        return new ClientRequestDTO(company.getTitle(), "A", "A_POTENTIAL", "B_PARTNER", company.getRegistrationNumber());
    }
}
