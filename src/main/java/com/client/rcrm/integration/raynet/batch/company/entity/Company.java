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
    private String email;
    private String phoneNumber;
}
