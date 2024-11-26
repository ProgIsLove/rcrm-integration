package com.client.rcrm.integration.raynet.batch.writer;

import com.client.rcrm.integration.raynet.batch.company.dao.CrudPaginationDAO;
import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class CompanyRecordItemWriter implements ItemWriter<Company> {

    private final CrudPaginationDAO companyDAO;

    public CompanyRecordItemWriter(CrudPaginationDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    @Override
    public void write(Chunk<? extends Company> companies) throws Exception {

        List<? extends Company> companyList = companies.getItems();

        companyDAO.saveAll(companyList);
    }
}
