package com.client.rcrm.integration.raynet.batch.company.dao;

import com.client.rcrm.integration.raynet.batch.company.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.batch.company.entity.Company;

import java.util.List;

public interface CompanyDAO {

    void saveAll(List<Company> companies);

//    <S extends T> void saveAll(ItemWriterrable<S> entities);
}
