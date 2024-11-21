package com.client.rcrm.integration.raynet.batch.reader;

import com.client.rcrm.integration.raynet.batch.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.batch.fieldmapper.CompanyRecordFieldSetMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class CompanyRecordItemReader {

    private static final String DEFAULT_DELIMITER = ";";
    private static final String REGISTRATION_NUMBER = "regNumber";
    private static final String TITLE = "title";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";

//    private FlatFileItemReader<CompanyDTO> delegate;
//
//
//    @Value("#{jobParameters['input.file']}")
//    private String inputFile;
//
//    @PostConstruct
//    public void init() {
//        this.delegate = new FlatFileItemReaderBuilder<CompanyDTO>()
//                .name("companyItemReader")
//                .resource(new FileSystemResource(inputFile))
//                .saveState(false)
//                .linesToSkip(1)
//                .lineMapper(lineMapper())
//                .targetType(CompanyDTO.class)
//                .build();
//    }

//    @Override
//    public FlatFileItemReader<CompanyDTO> read() {
//        return
//    }

    public LineMapper<CompanyDTO> lineMapper() {
        DefaultLineMapper<CompanyDTO> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(DEFAULT_DELIMITER);
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(REGISTRATION_NUMBER, TITLE, EMAIL, PHONE);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new CompanyRecordFieldSetMapper());

        return lineMapper;
    }
}
