package com.client.rcrm.integration.raynet.batch.jobconfig;

import com.client.rcrm.integration.raynet.batch.company.dao.CrudPaginationDAO;
import com.client.rcrm.integration.raynet.batch.company.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import com.client.rcrm.integration.raynet.batch.exception.InvalidEmailException;
import com.client.rcrm.integration.raynet.batch.exception.InvalidPhoneNumberException;
import com.client.rcrm.integration.raynet.batch.exception.InvalidRegistrationNumberException;
import com.client.rcrm.integration.raynet.batch.listener.ImportJobListener;
import com.client.rcrm.integration.raynet.batch.processor.ValidatingItemProcessor;
import com.client.rcrm.integration.raynet.batch.reader.CompanyRecordItemReader;
import com.client.rcrm.integration.raynet.batch.validation.ValidationService;
import com.client.rcrm.integration.raynet.batch.writer.CompanyRecordItemWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Objects;

@Configuration
@Slf4j
public class importCompanyRecordJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final ValidationService validationService;
    private final CrudPaginationDAO<?> companyDAO;

    public importCompanyRecordJobConfig(JobRepository jobRepository,
                                        PlatformTransactionManager platformTransactionManager,
                                        ValidationService validationService, CrudPaginationDAO<?> companyDAO) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.validationService = validationService;
        this.companyDAO = companyDAO;
    }

    @Bean
    public Job csvImportJob(JobRepository jobRepository, Step importCompanyStep, ImportJobListener listener) {
        return new JobBuilder("csvImportJob", jobRepository)
                .start(importCompanyStep)
                .listener(listener)
                .build();
    }

    @Bean(name = "validatingItemProcessor")
    public ValidatingItemProcessor processor() {
        return new ValidatingItemProcessor(validationService);
    }

    @Bean(name = "reader")
    @StepScope
    public FlatFileItemReader<CompanyDTO> reader(@Value("#{jobParameters['input.file']}") String inputFile) {
        CompanyRecordItemReader companyRecordItemReader = new CompanyRecordItemReader(inputFile);
        return companyRecordItemReader.read();
    }

    @Bean(name = "writer")
    public ItemWriter<Company> writer() {
        return new CompanyRecordItemWriter(companyDAO);
    }


    @Bean(name = "importCompanyStep")
    public Step importCompanyStep(@Qualifier("reader") FlatFileItemReader<CompanyDTO> reader) {
        return new StepBuilder("importCompanyStep", jobRepository)
                .<CompanyDTO, Company>chunk(10000, platformTransactionManager)
                .reader(Objects.requireNonNull(reader))
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .faultTolerant()
                .skip(InvalidEmailException.class)
                .skip(InvalidRegistrationNumberException.class)
                .skip(InvalidPhoneNumberException.class)
                .skipLimit(300)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new VirtualThreadTaskExecutor();
    }
}
