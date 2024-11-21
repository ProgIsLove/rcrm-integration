package com.client.rcrm.integration.raynet.batch.jobconfig;

import com.client.rcrm.integration.raynet.batch.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.batch.processor.ValidatingItemProcessor;
import com.client.rcrm.integration.raynet.batch.reader.CompanyRecordItemReader;
import com.client.rcrm.integration.raynet.batch.validation.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Objects;

@Configuration
@Slf4j
public class importCompanyRecordJobConfig {

    @Bean
    public Job runJob(JobRepository jobRepository, Step importCompanyStep) {
        return new JobBuilder("importCompanyJob", jobRepository)
                .start(importCompanyStep)
                .build();

    }

    @Bean(name = "validatingItemProcessor")
    public ValidatingItemProcessor processor(ValidationService validationService) {
        return new ValidatingItemProcessor(validationService);
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CompanyDTO> reader(@Value("#{jobParameters['input.file']}") String inputFile,
                                                 CompanyRecordItemReader companyRecordItemReader) {
        return new FlatFileItemReaderBuilder<CompanyDTO>()
                .name("companyItemReader")
                .resource(new FileSystemResource(inputFile))
                .saveState(false)
                .linesToSkip(1)
                .lineMapper(companyRecordItemReader.lineMapper())
                .targetType(CompanyDTO.class)
                .build();
    }

    @Bean(name = "importCompanyStep")
    public Step importCompanyStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  @Qualifier("reader") FlatFileItemReader<CompanyDTO> reader,
                                  ValidationService validationService) {
        return new StepBuilder("importCompanyStep", jobRepository)
                .<CompanyDTO, CompanyDTO>chunk(100, transactionManager)
                .reader(Objects.requireNonNull(reader))
                .processor(processor(validationService))
                .writer(items -> log.info("writing items: {}", items))
                .build();
    }
}
