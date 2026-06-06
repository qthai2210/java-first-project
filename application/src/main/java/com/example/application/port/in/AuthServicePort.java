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
}
