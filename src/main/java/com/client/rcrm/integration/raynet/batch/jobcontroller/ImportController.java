package com.client.rcrm.integration.raynet.batch.jobcontroller;

import com.client.rcrm.integration.raynet.batch.validation.ValidationService;
import com.client.rcrm.integration.raynet.connector.rcrmconnectr.RaynetService;
import com.client.rcrm.integration.raynet.connector.rcrmconnectr.dto.CompanyDTO;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/host/uploadData")
@Slf4j
public class ImportController {

    private final JobLauncher jobLauncher;
    private final Job job;
    private final ValidationService validationService;
    private final RaynetService raynetService;

    public ImportController(JobLauncher jobLauncher, Job job, ValidationService validationService, RaynetService raynetService) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.validationService = validationService;
        this.raynetService = raynetService;
    }

    @PostMapping
    @RateLimiter(name = "apiUploadDataRateLimit")
    public ResponseEntity<String> importCsvToDBJob(@RequestParam("file") MultipartFile file) throws BadRequestException {

        validationService.validateFile(file, ".csv");

        JobExecution run;

        try {
            Path targetDir = Paths.get("src/main/resources/data");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Path tempFile = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("input.file", tempFile.toAbsolutePath().toString())
                    .toJobParameters();


            run = jobLauncher.run(this.job, jobParameters);
//            if("COMPLETED".equalsIgnoreCase(run.getStatus().toString())){
//                Files.deleteIfExists(Paths.get(tempFile.toUri()));
//            }

        } catch (IOException e) {
            throw new BadRequestException("Error occurred while processing the file.", e);
        } catch (JobExecutionAlreadyRunningException
                 | JobRestartException
                 | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {
            log.error("Error executing job with parameters: {}", e.getMessage());
            throw new BadRequestException("Error executing job.", e);
        }

        return ResponseEntity.ok().body(run.getStatus().toString());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyDTO>> companies() {
        return ResponseEntity.ok(raynetService.getCompanies());
    }
}
