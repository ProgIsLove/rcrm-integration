package com.client.rcrm.integration.raynet.batch.fieldmapper;

import com.client.rcrm.integration.raynet.batch.dto.CompanyDTO;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class CompanyRecordFieldSetMapper implements FieldSetMapper<CompanyDTO> {
    @Override
    public CompanyDTO mapFieldSet(FieldSet fieldSet) {
        return new CompanyDTO(fieldSet.readString(0),
                fieldSet.readString(1),
                fieldSet.readString(2),
                fieldSet.readString(3));
    }
}
