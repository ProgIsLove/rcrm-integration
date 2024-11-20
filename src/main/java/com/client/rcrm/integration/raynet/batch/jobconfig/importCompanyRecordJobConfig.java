package com.client.rcrm.integration.raynet.batch.jobconfig;

import com.client.rcrm.integration.raynet.batch.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.batch.processor.ValidatingItemProcessor;
import com.client.rcrm.integration.raynet.batch.reader.CompanyRecordItemReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Objects;

@Configuration
@Slf4j
public class importCompanyRecordJobConfig {

    private final CompanyRecordItemReader companyRecordItemReader;


    public importCompanyRecordJobConfig(CompanyRecordItemReader companyRecordItemReader) {
        this.companyRecordItemReader = companyRecordItemReader;
    }

    @Bean(name = "validatingItemProcessor")
    public ValidatingItemProcessor processor() {
        return new ValidatingItemProcessor();
    }

    @Bean(name = "importCompanyStep")
    public Step importCompanyStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("importCompanyStep", jobRepository)
                .<CompanyDTO, CompanyDTO>chunk(100, transactionManager)
                .reader(Objects.requireNonNull(companyRecordItemReader.read()))
                .processor(processor())
                .writer(items -> log.info("writing items: {}", items))
                .build();
    }


}
