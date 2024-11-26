package com.client.rcrm.integration.raynet.batch.company.dao;

import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import com.client.rcrm.integration.raynet.batch.company.mapper.CompanyMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CompanyDAO implements CrudPaginationDAO<Company> {

    private final JdbcTemplate jdbcTemplate;

    public CompanyDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Transactional
    public void saveAll(List<Company> companies) {
        jdbcTemplate
                .batchUpdate("INSERT INTO COMPANY_INFORMATION (REGISTRATION_NUMBER, TITLE, EMAIL, PHONE_NUMBER) VALUES (?, ?, ?, ?)",
                        companies,
                        10000,
                        (PreparedStatement ps, Company company) -> {
                            ps.setString(1, company.getRegistrationNumber());
                            ps.setString(2, company.getTitle());
                            ps.setString(3, company.getEmail());
                            ps.setString(4, company.getPhoneNumber());
                        });
    }

    @Override
    public List<Company> getAllCompanies(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM COMPANY_INFORMATION LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new Object[]{pageSize, offset}, new CompanyMapper());
    }
}
