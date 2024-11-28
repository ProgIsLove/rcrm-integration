package com.client.rcrm.integration.raynet.batch.writer;

import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import com.client.rcrm.integration.raynet.batch.company.repository.CompanyRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class CompanyRecordItemWriter implements ItemWriter<Company> {

    private final CompanyRepository companyRepository;

    public CompanyRecordItemWriter(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public void write(Chunk<? extends Company> companies) {

        companyRepository.saveAll(companies);
    }
}
