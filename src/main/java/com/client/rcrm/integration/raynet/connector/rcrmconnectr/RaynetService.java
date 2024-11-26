package com.client.rcrm.integration.raynet.connector.rcrmconnectr;

import com.client.rcrm.integration.raynet.batch.company.dao.CompanyDAO;
import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto.ClientDTO;
import com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto.ClientRequestDTO;
import com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto.ClientResponseDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class RaynetService {

    private final RaynetConnector raynetConnector;
    private final CompanyDAO companyDAO;

    public RaynetService(RaynetConnector raynetConnector, CompanyDAO companyDAO) {
        this.raynetConnector = raynetConnector;
        this.companyDAO = companyDAO;
    }

//    public void syncCompaniesWithRaynetPaginated(int pageSize) {
//        int page = 1;
//        List<Company> companies;
//
//        // Loop through pages
//        do {
//            companies = companyDAO.getAllCompanies(page, pageSize);
//
//            // Process each company synchronously
//            for (Company company : companies) {
////                try {
////                    ClientResponseDTO clientResponseDTO = checkCompanyExistByRegNumber(company.getRegistrationNumber());
////                    if (clientResponseDTO.totalCount() != 0) {
////                        // If company exists, update it
////                        List<ClientDTO> data = clientResponseDTO.data();
////                        updateCompany(data.getFirst().id(), mapToClientRequestDTO(company));
////                    } else {
////                        // If company doesn't exist, create it
////                        createCompany(mapToClientRequestDTO(company));
////                    }
////                } catch (Exception e) {
////                    System.err.println("Error processing company: " + company.getRegistrationNumber() + " - " + e.getMessage());
////                }
//            }
//
//            // Increment the page number
//            page++;
//        } while (!companies.isEmpty()); // Continue looping until no companies are left
//    }

    public void syncCompaniesWithRaynetPaginated(int pageSize) {
        int page = 1;
        List<Company> companies;

        do {
            companies = companyDAO.getAllCompanies(page, pageSize);

            List<CompletableFuture<Void>> tasks = companies.stream()
                    .map(this::processCompany)
                    .toList();

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

            page++;
        } while (!companies.isEmpty());
    }

    @Async
    public CompletableFuture<Void> processCompany(Company company) {
        return checkCompanyExistByRegNumber(company.getRegistrationNumber())
                .thenAccept(clientResponseDTO -> {
                    if (clientResponseDTO.totalCount() != 0) {
                        clientResponseDTO.data().forEach(client -> updateCompany(client.id(), mapToClientRequestDTO(company)));
                    } else {
                        createCompany(mapToClientRequestDTO(company));
                    }
                })
                .exceptionally(ex -> {
                    // Handle error
                    System.err.println("Error: " + ex.getMessage());
                    return null;
                });
    }


    public CompletableFuture<Map<String, Object>> createCompany(ClientRequestDTO clientRequestDTO) {
        return CompletableFuture.supplyAsync(() -> raynetConnector.createCompany(clientRequestDTO));
    }

    public CompletableFuture<Map<String, String>> updateCompany(Long id, ClientRequestDTO clientRequestDTO) {
        return CompletableFuture.supplyAsync(() -> raynetConnector.updateCompany(id, clientRequestDTO));

    }

    public CompletableFuture<ClientResponseDTO> checkCompanyExistByRegNumber(String regNumber) {
        return CompletableFuture.supplyAsync(() -> raynetConnector.fetchCompanyByRegNumber(regNumber));
    }

    private ClientRequestDTO mapToClientRequestDTO(Company company) {
        return new ClientRequestDTO(company.getTitle(), "A", "A_POTENTIAL", "B_PARTNER", company.getRegistrationNumber());
    }
}
