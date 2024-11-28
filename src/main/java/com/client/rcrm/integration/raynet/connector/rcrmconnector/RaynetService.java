package com.client.rcrm.integration.raynet.connector.rcrmconnector;

import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import com.client.rcrm.integration.raynet.batch.company.repository.CompanyRepository;
import com.client.rcrm.integration.raynet.connector.rcrmconnector.dto.ClientRequestDTO;
import com.client.rcrm.integration.raynet.connector.rcrmconnector.dto.ClientResponseDTO;
import com.client.rcrm.integration.raynet.connector.rcrmconnector.mapper.ReynetMapper;
import com.client.rcrm.integration.raynet.notification.NotificationService;
import com.client.rcrm.integration.raynet.notification.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationService notificationService;
    private final CompanyRepository companyRepository;
    private final ReynetMapper raynetMapper;

    public RaynetService(RaynetConnector raynetConnector,
                         RedisTemplate<String, Object> redisTemplate,
                         NotificationService notificationService,
                         CompanyRepository companyRepository, ReynetMapper raynetMapper) {
        this.raynetConnector = raynetConnector;
        this.redisTemplate = redisTemplate;
        this.notificationService = notificationService;
        this.companyRepository = companyRepository;
        this.raynetMapper = raynetMapper;
    }

    public void syncCompaniesWithRaynetPaginated(int pageSize) {
        int page = 1;
        Page<Company> companies;
        boolean allSuccess = true;

        do {
            companies = companyRepository.findAll(PageRequest.of(page, pageSize));

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

        } while (!companies.isEmpty());

        companyRepository.deleteAll();

        if (allSuccess) {
            notificationService.sendNotification(NotificationType.EMAIL, "json_success.json");
        }
    }

    @Async
    public CompletableFuture<Void> processCompany(Company company) {
        return checkCompanyExistByRegNumber(company.getRegistrationNumber())
                .thenAccept(clientResponseDTO -> {
                    if (clientResponseDTO.totalCount() != 0) {
                        clientResponseDTO.data()
                                .forEach(client -> updateCompany(client.id(), raynetMapper.mapToClientRequestDTO(company)));
                    } else {
                        createCompany(raynetMapper.mapToClientRequestDTO(company));
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
}
