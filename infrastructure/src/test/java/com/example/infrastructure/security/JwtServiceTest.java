package com.example.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void testValidateSecretKeyStrengthWithValidKey() {
        JwtService jwtService = new JwtService();
        // A valid 32-character (256-bit) key
        String validKey = "development_jwt_secret_key_must_be_at_least_32_bytes_long_for_signing";
        ReflectionTestUtils.setField(jwtService, "secretKey", validKey);

        assertDoesNotThrow(jwtService::validateSecretKeyStrength);
    }

    @Test
    void testValidateSecretKeyStrengthWithTooShortKey() {
        JwtService jwtService = new JwtService();
        // An invalid 16-character key
        String shortKey = "too_short_secret";
        ReflectionTestUtils.setField(jwtService, "secretKey", shortKey);

        IllegalStateException ex = assertThrows(IllegalStateException.class, jwtService::validateSecretKeyStrength);
        assertTrue(ex.getMessage().contains("too weak"));
        assertTrue(ex.getMessage().contains("at least 32 bytes"));
    }

    @Test
    void testValidateSecretKeyStrengthWithNullKey() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", null);

        IllegalStateException ex = assertThrows(IllegalStateException.class, jwtService::validateSecretKeyStrength);
        assertTrue(ex.getMessage().contains("must not be empty or null"));
    }

    @Test
    void testValidateSecretKeyStrengthWithBlankKey() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "   ");

        IllegalStateException ex = assertThrows(IllegalStateException.class, jwtService::validateSecretKeyStrength);
        assertTrue(ex.getMessage().contains("must not be empty or null"));
    }
}
