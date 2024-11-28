package com.client.rcrm.integration.raynet.batch.company.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@Entity(name = "company")
public class Company implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
