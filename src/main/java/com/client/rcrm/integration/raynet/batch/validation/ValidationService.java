package com.client.rcrm.integration.raynet.batch.validation;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    // Regular expression for validating an email address
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Regular expression for validating a Czech phone number format
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+420[0-9]{9}$");

    // Regular expression for validating registration number (Czech IČO)
    private static final Pattern REGISTRATION_NUMBER_PATTERN =
            Pattern.compile("^[0-9]{8}$");

    public boolean isEmailValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isRegistrationNumberValid(String regNumber) {
        return regNumber != null && REGISTRATION_NUMBER_PATTERN.matcher(regNumber).matches();
    }

    public boolean isPhoneNumberValid(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public void validateFile(MultipartFile file, String fileExtension) throws BadRequestException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty. Please upload a valid CSV file.");
        }

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(fileExtension)) {
            throw new BadRequestException("Invalid file format. Please upload a .csv file.");
        }
    }
}
