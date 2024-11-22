package com.client.rcrm.integration.raynet.batch.company.mapper;

import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyMapper implements RowMapper<Company> {
    @Override
    public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Company.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .registrationNumber(rs.getString("registration_number"))
                .phoneNumber(rs.getString("phone_number"))
                .build();
    }
}
