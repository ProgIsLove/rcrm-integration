package com.client.rcrm.integration.raynet.batch.company.repository;

import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CompanyRepository extends PagingAndSortingRepository<Company, Long>, CrudRepository<Company, Long> {
}
