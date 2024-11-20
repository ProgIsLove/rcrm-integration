package com.client.rcrm.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class RcrmIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RcrmIntegrationApplication.class, args);
    }

}
