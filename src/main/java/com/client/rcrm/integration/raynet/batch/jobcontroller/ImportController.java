package com.client.rcrm.integration.raynet.batch.jobcontroller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RestController
@RequestMapping("/host/uploadData")
@Slf4j
public class ImportController {

    private final JobLauncher jobLauncher;
    private final Job job;

    public ImportController(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @PostMapping
    @RateLimiter(name = "apiUploadDataRateLimit")
    public ResponseEntity<String> importCsvToDBJob(@RequestParam("file") MultipartFile file) throws BadRequestException {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty. Please upload a valid CSV file.");
        }

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new BadRequestException("Invalid file format. Please upload a .csv file.");
        }

        JobExecution run;

        try {
            Path targetDir = Paths.get("src/main/resources/data");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir); // Ensure the directory exists
            }
            Path tempFile = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("input.file", tempFile.toAbsolutePath().toString())
                    .toJobParameters();


            run = jobLauncher.run(this.job, jobParameters);
            if("COMPLETED".equalsIgnoreCase(run.getStatus().toString())){
                Files.deleteIfExists(Paths.get(tempFile.toUri()));
            }

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
}
