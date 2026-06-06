package com.example.application.port.out;

/**
 * Output port for password encoding operations.
 * The Application layer depends on this interface, not on BCrypt directly.
 */
public interface PasswordEncoderPort {

    /**
     * Encodes the raw password.
     *
     * @param rawPassword the raw password to encode
     * @return the encoded password
     */
    String encode(String rawPassword);

    /**
     * Verifies that a raw password matches an encoded password.
     *
     * @param rawPassword     the raw password to check
     * @param encodedPassword the encoded password to check against
     * @return true if the passwords match
     */
    boolean matches(String rawPassword, String encodedPassword);
}
