package com.client.rcrm.integration.raynet.batch.reader;

import com.client.rcrm.integration.raynet.batch.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.batch.fieldmapper.CompanyRecordFieldSetMapper;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.stereotype.Component;

@Component
public class CompanyRecordItemReader implements ItemReader<FlatFileItemReader<CompanyDTO>> {

    private static final String DEFAULT_DELIMITER = ";";
    private static final String REGISTRATION_NUMBER = "regNumber";
    private static final String TITLE = "title";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";

    @Override
    public FlatFileItemReader<CompanyDTO> read() {
        return new FlatFileItemReaderBuilder<CompanyDTO>()
                .name("company item reader")
                .saveState(Boolean.FALSE)
                .linesToSkip(1)
                .lineMapper(lineMapper())
                .targetType(CompanyDTO.class)
                .build();
    }

    private LineMapper<CompanyDTO> lineMapper() {
        DefaultLineMapper<CompanyDTO> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(DEFAULT_DELIMITER);
        lineTokenizer.setNames(REGISTRATION_NUMBER, TITLE, EMAIL, PHONE);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new CompanyRecordFieldSetMapper());

        return lineMapper;
    }
}
