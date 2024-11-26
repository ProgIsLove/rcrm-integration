//package com.client.rcrm.integration.raynet.batch.company.dao;
//
//import com.client.rcrm.integration.raynet.batch.company.entity.Company;
//import com.client.rcrm.integration.raynet.batch.company.entity.RaynetClient;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.sql.PreparedStatement;
//import java.util.List;
//
//@Repository
//public class RaynetClientDAO implements CrudPaginationDAO<RaynetClient> {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public RaynetClientDAO(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @Override
//    @Transactional
//    public void saveAll(List<RaynetClient> clients) {
//        jdbcTemplate
//                .batchUpdate("INSERT INTO RAYNET_CLIENT (NAME, REGISTRATION_NUMBER, CLIENT_ID) VALUES (?, ?, ?)",
//                        clients,
//                        1000,
//                        (PreparedStatement ps, RaynetClient client) -> {
//                            ps.setString(1, client.getName());
//                            ps.setString(2, client.getRegistrationNumber());
//                            ps.setLong(3, client.getClientId());
//                        });
//    }
//
//    @Override
//    public List<RaynetClient> getAllCompanies(int page, int size) {
//        return List.of();
//    }
//
//    @Override
//    public int count() {
//        return 0;
//    }
//}
