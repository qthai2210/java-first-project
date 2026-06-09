package com.example.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidPassword() {
        UserRequestDto dto = UserRequestDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("SuperSecret123!") // Valid: 15 chars, uppercase, lowercase, digit, special char, no space
                .build();

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid password should not produce any validation errors");
    }

    @Test
    void testPasswordTooShort() {
        UserRequestDto dto = UserRequestDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("Short1!") // Only 7 characters
                .build();

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 12 and 128 characters")));
    }

    @Test
    void testPasswordMissingUppercase() {
        UserRequestDto dto = UserRequestDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("lowercase123!") // Missing uppercase
                .build();

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("at least one uppercase letter")));
    }   

    @Test
    void testPasswordMissingDigit() {
        UserRequestDto dto = UserRequestDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("NoDigitsHere!") // Missing digit
                .build();

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("one number")));
    }

    @Test
    void testPasswordMissingSpecialChar() {
        UserRequestDto dto = UserRequestDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("NoSpecial1234") // Missing special character
                .build();

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("one special character")));
    }

    @Test
    void testPasswordContainsSpaces() {
        UserRequestDto dto = UserRequestDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("Super Secret 123!") // Contains space
                .build();

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("no spaces")));
    }
}
