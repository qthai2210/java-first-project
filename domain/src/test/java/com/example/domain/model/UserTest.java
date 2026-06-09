package com.example.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserImmutabilityAndBuilder() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("hash123")
                .role(Role.USER)
                .createdAt(now)
                .build();

        // Verify values
        assertEquals(1L, user.getId());
        assertEquals("John", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("hash123", user.getPassword());
        assertEquals(Role.USER, user.getRole());
        assertEquals(now, user.getCreatedAt());

        // Verify that using toBuilder creates a new instance with changes, leaving original untouched
        User updatedUser = user.toBuilder()
                .name("Jane")
                .role(Role.ADMIN)
                .build();

        assertNotSame(user, updatedUser);
        assertEquals("John", user.getName());
        assertEquals("Jane", updatedUser.getName());
        assertEquals(Role.USER, user.getRole());
        assertEquals(Role.ADMIN, updatedUser.getRole());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.getCreatedAt(), updatedUser.getCreatedAt());
    }

    @Test
    void testToStringExcludesPassword() {
        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("superSecretPassword")
                .role(Role.USER)
                .build();

        String toStringStr = user.toString();
        
        // Assertions
        assertTrue(toStringStr.contains("John"));
        assertTrue(toStringStr.contains("john@example.com"));
        assertFalse(toStringStr.contains("superSecretPassword"));
        assertFalse(toStringStr.contains("password"));
    }

    @Test
    void testEqualsAndHashCodeOnlyIncludesIdAndEmail() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("hash1")
                .role(Role.USER)
                .createdAt(now)
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("Jane")
                .email("john@example.com")
                .password("hash2")
                .role(Role.ADMIN)
                .createdAt(now.plusDays(1))
                .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
