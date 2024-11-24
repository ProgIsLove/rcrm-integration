package com.client.rcrm.integration.raynet.connector.rcrmconnectr;

import com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto.CompanyDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaynetService {

    private final RaynetConnector raynetConnector;

    public RaynetService(RaynetConnector raynetConnector) {
        this.raynetConnector = raynetConnector;
    }

    public List<CompanyDTO> getCompanies() {
       return raynetConnector.getCompanies();
    }
}
