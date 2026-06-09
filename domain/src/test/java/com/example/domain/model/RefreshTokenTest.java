package com.example.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    @Test
    void testRefreshTokenImmutabilityAndBuilder() {
        User user = User.builder().id(1L).email("john@example.com").build();
        Instant expiry = Instant.now().plusSeconds(60);
        
        RefreshToken token = RefreshToken.builder()
                .id(1L)
                .token("uuid-token-123")
                .expiryDate(expiry)
                .user(user)
                .build();

        assertEquals(1L, token.getId());
        assertEquals("uuid-token-123", token.getToken());
        assertEquals(expiry, token.getExpiryDate());
        assertEquals(user, token.getUser());

        RefreshToken updated = token.toBuilder()
                .token("uuid-token-456")
                .build();

        assertNotSame(token, updated);
        assertEquals("uuid-token-123", token.getToken());
        assertEquals("uuid-token-456", updated.getToken());
        assertEquals(token.getExpiryDate(), updated.getExpiryDate());
        assertEquals(token.getUser(), updated.getUser());
    }

    @Test
    void testIsExpired() {
        Instant past = Instant.now().minusSeconds(10);
        Instant future = Instant.now().plusSeconds(10);

        RefreshToken expiredToken = RefreshToken.builder()
                .expiryDate(past)
                .build();

        RefreshToken validToken = RefreshToken.builder()
                .expiryDate(future)
                .build();

        assertTrue(expiredToken.isExpired());
        assertFalse(validToken.isExpired());
    }

    @Test
    void testEqualsAndHashCodeOnlyIncludesToken() {
        RefreshToken token1 = RefreshToken.builder()
                .id(1L)
                .token("matching-token")
                .expiryDate(Instant.now())
                .build();

        RefreshToken token2 = RefreshToken.builder()
                .id(2L)
                .token("matching-token")
                .expiryDate(Instant.now().plusSeconds(100))
                .build();

        assertEquals(token1, token2);
        assertEquals(token1.hashCode(), token2.hashCode());
    }
}
