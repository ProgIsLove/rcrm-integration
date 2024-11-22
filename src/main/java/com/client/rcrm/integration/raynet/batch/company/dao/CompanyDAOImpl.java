package com.client.rcrm.integration.raynet.batch.company.dao;

import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CompanyDAOImpl implements CompanyDAO {

    private final JdbcTemplate jdbcTemplate;

    public CompanyDAOImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean saveAll(List<Company> companies) {
        return false;
    }
}
