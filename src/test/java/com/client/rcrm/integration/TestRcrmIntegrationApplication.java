package com.client.rcrm.integration;

import org.springframework.boot.SpringApplication;

public class TestRcrmIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.from(RcrmIntegrationApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
