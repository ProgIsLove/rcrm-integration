package com.client.rcrm.integration.raynet.batch.company.mapper;

import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyMapper implements RowMapper<Company> {
    @Override
    public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Company.builder()
                .id(rs.getLong(1))
                .registrationNumber(rs.getString(2))
                .title(rs.getString(3))
                .email(rs.getString(4))
                .phoneNumber(rs.getString(5))
                .build();
    }
}
