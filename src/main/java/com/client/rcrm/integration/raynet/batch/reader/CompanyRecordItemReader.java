package com.client.rcrm.integration.raynet.batch.reader;

import com.client.rcrm.integration.raynet.batch.company.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.batch.fieldmapper.CompanyRecordFieldSetMapper;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;



public class CompanyRecordItemReader implements ItemReader<FlatFileItemReader<CompanyDTO>> {

    private static final String DEFAULT_DELIMITER = ";";
    private static final String REGISTRATION_NUMBER = "regNumber";
    private static final String TITLE = "title";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";

    private final FlatFileItemReader<CompanyDTO> delegate;
    private final Object lock;

    public CompanyRecordItemReader(@Value("#{jobParameters['input.file']}") String filePath) {
        this.delegate = new FlatFileItemReaderBuilder<CompanyDTO>()
                .name("companyItemReader")
                .resource(new FileSystemResource(filePath))
                .saveState(false)
                .linesToSkip(1)
                .lineMapper(lineMapper())
                .targetType(CompanyDTO.class)
                .build();
        this.lock = new Object();
    }

    @Override
    public FlatFileItemReader<CompanyDTO> read() {
        synchronized (lock) {
            return this.delegate;
        }
    }

    private LineMapper<CompanyDTO> lineMapper() {
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
