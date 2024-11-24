package com.client.rcrm.integration.raynet.batch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
//@EnableConfigurationProperties
//@ConfigurationProperties(prefix = "database")
//@Getter
//@Setter
public class DbConfig {

//    private String url;
//    private String user;
//    private String password;
//    private String driver;

//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setUrl(this.url);
//        dataSource.setUsername(this.user);
//        dataSource.setPassword(this.password);
//        dataSource.setDriverClassName(this.driver);
//        return dataSource;
//    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
