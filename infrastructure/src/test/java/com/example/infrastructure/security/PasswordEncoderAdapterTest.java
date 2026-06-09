package com.example.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordEncoderAdapterTest {

    private PasswordEncoder mockSpringEncoder;
    private PasswordEncoderAdapter adapter;

    @BeforeEach
    void setUp() {
        mockSpringEncoder = mock(PasswordEncoder.class);
        adapter = new PasswordEncoderAdapter(mockSpringEncoder);
    }

    @Test
    void encodeDelegatesToSpringEncoder() {
        String raw = "rawPassword123";
        String encoded = "encodedPassword123";
        when(mockSpringEncoder.encode(raw)).thenReturn(encoded);

        String result = adapter.encode(raw);

        assertEquals(encoded, result);
        verify(mockSpringEncoder).encode(raw);
    }

    @Test
    void matchesDelegatesToSpringEncoder() {
        String raw = "rawPassword123";
        String encoded = "encodedPassword123";
        when(mockSpringEncoder.matches(raw, encoded)).thenReturn(true);

        boolean result = adapter.matches(raw, encoded);

        assertTrue(result);
        verify(mockSpringEncoder).matches(raw, encoded);
    }

    @Test
    void verifyCostFactorUsingActualEncoder() {
        // Test with a real BCrypt encoder configured with cost 12
        PasswordEncoder realSpringEncoder = new BCryptPasswordEncoder(12);
        PasswordEncoderAdapter realAdapter = new PasswordEncoderAdapter(realSpringEncoder);

        String raw = "MyStrongPassword123!";
        String hash = realAdapter.encode(raw);

        // BCrypt prefix for cost factor 12 is "$2a$12$"
        assertTrue(hash.startsWith("$2a$12$"), "Hash should start with $2a$12$ indicating cost factor 12");
        assertTrue(realAdapter.matches(raw, hash), "Password should match its own hash");
        assertFalse(realAdapter.matches("wrongPassword", hash), "Wrong password should not match the hash");
    }
}
