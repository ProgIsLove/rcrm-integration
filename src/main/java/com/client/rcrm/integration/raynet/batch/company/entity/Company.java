package com.client.rcrm.integration.raynet.batch.company.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class Company implements Serializable {

    private Long id;
    private String registrationNumber;
    private String title;
    private String email;
    private String phoneNumber;

    public Company() {
    }

    public Company(Long id, String registrationNumber, String title, String email, String phoneNumber) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.title = title;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
