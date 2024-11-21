package com.client.rcrm.integration.raynet.batch.jobcontrol;

import com.client.rcrm.integration.raynet.batch.reader.CompanyRecordItemReader;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@RestController
@RequestMapping("/host/uploadData")
public class ImportController {

    private final JobLauncher jobLauncher;
    private final Job job;
    private final CompanyRecordItemReader companyRecordItemReader;

    public ImportController(JobLauncher jobLauncher, Job job, CompanyRecordItemReader companyRecordItemReader) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.companyRecordItemReader = companyRecordItemReader;
    }

    @PostMapping
    @RateLimiter(name = "apiUploadDataRateLimit")
    public void importCsvToDBJob(@RequestParam("file") MultipartFile file) throws BadRequestException {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty. Please upload a valid CSV file.");
        }

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new BadRequestException("Invalid file format. Please upload a .csv file.");
        }

        try {
            Path tempFile = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("input.file", tempFile.toAbsolutePath().toString())
                    .toJobParameters();

            jobLauncher.run(this.job, jobParameters);

        } catch (IOException e) {
            throw new BadRequestException("Error occurred while processing the file.", e);
        } catch (JobExecutionAlreadyRunningException
                 | JobRestartException
                 | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
}
