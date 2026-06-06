package com.example.application.port.out;

import com.example.domain.model.User;

/**
 * Output port for JWT token operations.
 * The Application layer depends on this interface, not on JJWT directly.
 */
public interface JwtServicePort {

    /**
     * Generates a JWT token for the given user.
     *
     * @param user the authenticated user
     * @return a signed JWT token string
     */
    String generateToken(User user);

    /**
     * Extracts the email (subject) from a JWT token.
     *
     * @param token the JWT token string
     * @return the email stored in the token's subject claim
     */
    String extractEmail(String token);

    /**
     * Validates that a JWT token is valid for the given user.
     *
     * @param token the JWT token string
     * @param user  the user to validate against
     * @return true if the token is valid and not expired
     */
    boolean isTokenValid(String token, User user);
}
