package com.client.rcrm.integration.raynet.batch.company.mapper;

import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyMapper implements RowMapper<Company> {
    @Override
    public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Company(rs.getLong(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5));
    }
}
