package com.client.rcrm.integration.raynet.batch.processor;

import com.client.rcrm.integration.raynet.batch.company.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.batch.company.entity.Company;
import com.client.rcrm.integration.raynet.batch.exception.InvalidEmailException;
import com.client.rcrm.integration.raynet.batch.exception.InvalidPhoneNumberException;
import com.client.rcrm.integration.raynet.batch.exception.InvalidRegistrationNumberException;
import com.client.rcrm.integration.raynet.batch.validation.ValidationService;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;

@JobScope
public class ValidatingItemProcessor implements ItemProcessor<CompanyDTO, Company> {

    private final ValidationService validationService;

    public ValidatingItemProcessor(ValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    public Company process(CompanyDTO companyDTO) {

        if (!this.validationService.isEmailValid(companyDTO.email())) {
            throw new InvalidEmailException("Invalid email for " + companyDTO);
        }

        if (!this.validationService.isRegistrationNumberValid(companyDTO.regNumber())) {
            throw new InvalidRegistrationNumberException("Invalid registration number for " + companyDTO);
        }

        if (!this.validationService.isPhoneNumberValid(companyDTO.phone())) {
            throw new InvalidPhoneNumberException("Invalid phone number for " + companyDTO);
        }

        return new Company(null, companyDTO.regNumber(), companyDTO.title(), companyDTO.email(), companyDTO.phone());
    }
}
