package com.client.rcrm.integration.raynet.batch.processor;

import com.client.rcrm.integration.raynet.batch.dto.CompanyDTO;
import com.client.rcrm.integration.raynet.batch.exception.InvalidEmailException;
import com.client.rcrm.integration.raynet.batch.exception.InvalidPhoneNumberException;
import com.client.rcrm.integration.raynet.batch.exception.InvalidRegistrationNumberException;
import com.client.rcrm.integration.raynet.batch.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class ValidatingItemProcessor implements ItemProcessor<CompanyDTO, CompanyDTO> {

    private ValidationService validationService;

    @Override
    public CompanyDTO process(CompanyDTO companyDTO) throws Exception {

        if (!this.validationService.isEmailValid(companyDTO.email())) {
            throw new InvalidEmailException("Invalid email for " + companyDTO);
        }

        if (!this.validationService.isRegistrationNumberValid(companyDTO.regNumber())) {
            throw new InvalidRegistrationNumberException("Invalid registration number for " + companyDTO);
        }

        if (!this.validationService.isPhoneNumberValid(companyDTO.phone())) {
            throw new InvalidPhoneNumberException("Invalid phone number for " + companyDTO);
        }

        return companyDTO;
    }
}
