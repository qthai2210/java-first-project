package com.example.application.port.in;

import com.example.application.dto.AuthRequestDto;
import com.example.application.dto.AuthResponseDto;
import com.example.application.dto.UserRequestDto;

/**
 * Input port (use case) for authentication operations.
 */
public interface AuthServicePort {

    /**
     * Registers a new user and returns a JWT token.
     *
     * @param request the user registration data
     * @return an AuthResponseDto containing the JWT access token
     */
    AuthResponseDto register(UserRequestDto request);

    /**
     * Authenticates a user with email and password, then returns a JWT token.
     *
     * @param request the login credentials
     * @return an AuthResponseDto containing the JWT access token
     */
    AuthResponseDto login(AuthRequestDto request);

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param refreshToken the refresh token string
     * @return a new AuthResponseDto containing a new access token and refresh token
     */
    AuthResponseDto refreshToken(String refreshToken);
}
