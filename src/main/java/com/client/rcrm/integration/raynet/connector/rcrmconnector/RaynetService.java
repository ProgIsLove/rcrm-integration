package com.client.rcrm.integration.raynet.connector.rcrmconnector;

import com.client.rcrm.integration.raynet.batch.company.dao.CompanyDAO;
import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import com.client.rcrm.integration.raynet.connector.rcrmconnector.dto.ClientRequestDTO;
import com.client.rcrm.integration.raynet.connector.rcrmconnector.dto.ClientResponseDTO;
import com.client.rcrm.integration.raynet.notification.NotificationDetails;
import com.client.rcrm.integration.raynet.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RaynetService {

    private final RaynetConnector raynetConnector;
    private final CompanyDAO companyDAO;
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationService notificationService;

    public RaynetService(RaynetConnector raynetConnector, CompanyDAO companyDAO,
                         RedisTemplate<String, Object> redisTemplate, NotificationService notificationService) {
        this.raynetConnector = raynetConnector;
        this.companyDAO = companyDAO;
        this.redisTemplate = redisTemplate;
        this.notificationService = notificationService;
    }

    public void syncCompaniesWithRaynetPaginated(int pageSize) {
        int page = 1;
        List<Company> companies;
        boolean allSuccess = true;

        do {
            companies = companyDAO.getAllCompanies(page, pageSize);

            List<CompletableFuture<Void>> tasks = companies.stream()
                    .map(this::processCompany)
                    .toList();

            try {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
            } catch (CompletionException ex) {
                log.error("Error occurred during company synchronization: {}", ex.getMessage());
                allSuccess = false;
            }

            page++;

            if (companies.isEmpty() && allSuccess) {
                notificationService.sendNotification("EmailWithAttachmentNotification", "json_success.json");
            }
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
                    switch (ex) {
                        case HttpServerErrorException exception:
                            throw new RaynetException(exception.getStatusCode(), exception.getMessage());

                        case HttpClientErrorException exception:
                            throw new RaynetException(exception.getStatusCode(), exception.getMessage());

                        case CompletionException exception:
                            if (exception.getCause() instanceof RaynetException raynetException) {
                                throw new RaynetException(raynetException.getStatusCode(), raynetException.getTranslatedMessage());
                            }

                        default:
                            throw new RuntimeException(ex.getMessage());
                    }
                });
    }


    public void createCompany(ClientRequestDTO clientRequestDTO) {
        CompletableFuture.supplyAsync(() -> raynetConnector.createCompany(clientRequestDTO));
    }

    public void updateCompany(Long id, ClientRequestDTO clientRequestDTO) {
        CompletableFuture.supplyAsync(() -> raynetConnector.updateCompany(id, clientRequestDTO));
    }

    public CompletableFuture<ClientResponseDTO> checkCompanyExistByRegNumber(String regNumber) {
        return CompletableFuture.supplyAsync(() -> {

            ClientResponseDTO cachedResponse = (ClientResponseDTO) redisTemplate.opsForValue().get(regNumber);

            if (cachedResponse != null) {
                return cachedResponse;
            }

            ClientResponseDTO response = raynetConnector.fetchCompanyByRegNumber(regNumber);

            redisTemplate.opsForValue().set(regNumber, response, 5, TimeUnit.DAYS);

            return response;
        });
    }

    private ClientRequestDTO mapToClientRequestDTO(Company company) {
        return new ClientRequestDTO(company.getTitle(), "A", "A_POTENTIAL", "B_PARTNER", company.getRegistrationNumber());
    }
}
