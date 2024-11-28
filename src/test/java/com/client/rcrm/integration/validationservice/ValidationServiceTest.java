package com.client.rcrm.integration.validationservice;

import com.client.rcrm.integration.raynet.batch.validation.ValidationService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }

    @Test
    void isEmailValid_shouldReturnTrue_forValidEmail() {
        String validEmail = "test@example.com";
        assertTrue(validationService.isEmailValid(validEmail));
    }

    @Test
    void isEmailValid_shouldReturnFalse_forInvalidEmail() {
        String invalidEmail = "test@.com";
        assertFalse(validationService.isEmailValid(invalidEmail));
    }

    @Test
    void isEmailValid_shouldReturnFalse_forNullEmail() {
        assertFalse(validationService.isEmailValid(null));
    }

    @Test
    void isRegistrationNumberValid_shouldReturnTrue_forValidRegistrationNumber() {
        String validRegNumber = "12345678";
        assertTrue(validationService.isRegistrationNumberValid(validRegNumber));
    }

    @Test
    void isRegistrationNumberValid_shouldReturnFalse_forInvalidRegistrationNumber() {
        String invalidRegNumber = "12345";
        assertFalse(validationService.isRegistrationNumberValid(invalidRegNumber));
    }

    @Test
    void isRegistrationNumberValid_shouldReturnFalse_forNullRegistrationNumber() {
        assertFalse(validationService.isRegistrationNumberValid(null));
    }

    @Test
    void isPhoneNumberValid_shouldReturnTrue_forValidPhoneNumber() {
        String validPhone = "+420123456789";
        assertTrue(validationService.isPhoneNumberValid(validPhone));
    }

    @Test
    void isPhoneNumberValid_shouldReturnFalse_forInvalidPhoneNumber() {
        String invalidPhone = "123456789";
        assertFalse(validationService.isPhoneNumberValid(invalidPhone));
    }

    @Test
    void isPhoneNumberValid_shouldReturnFalse_forNullPhoneNumber() {
        assertFalse(validationService.isPhoneNumberValid(null));
    }

    @Test
    void isFile_shouldNotThrowException_forValidFileValid() {
        MockMultipartFile validFile = new MockMultipartFile(
                "file", "test.csv", "text/csv", "some,data".getBytes()
        );
        assertDoesNotThrow(() -> validationService.isFileValid(validFile, ".csv"));
    }

    @Test
    void isFile_shouldThrowException_forEmptyFileValid() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.csv", "text/csv", new byte[0]
        );
        Exception exception = assertThrows(BadRequestException.class,
                () -> validationService.isFileValid(emptyFile, ".csv"));

        assertEquals("File is empty. Please upload a valid CSV file.", exception.getMessage());
    }

    @Test
    void isFile_shouldThrowException_forInvalidFileValidExtension() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "some,data".getBytes()
        );
        Exception exception = assertThrows(BadRequestException.class,
                () -> validationService.isFileValid(invalidFile, ".csv"));

        assertEquals("Invalid file format. Please upload a .csv file.", exception.getMessage());
    }

    @Test
    void isFile_shouldThrowException_forNullFileValid() {
        Exception exception = assertThrows(BadRequestException.class,
                () -> validationService.isFileValid(null, ".csv"));

        assertEquals("File is empty. Please upload a valid CSV file.", exception.getMessage());
    }
}
