package com.client.rcrm.integration.raynet.batch.company.dao;

import com.client.rcrm.integration.raynet.batch.company.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.batch.company.entity.Company;

import java.util.List;

public interface CompanyDAO {

    boolean saveAll(List<Company> companies);
}
