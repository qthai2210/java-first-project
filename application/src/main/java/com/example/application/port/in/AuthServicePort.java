package com.example.application.port.in;

import com.example.application.command.LoginCommand;
import com.example.application.command.RegisterUserCommand;
import com.example.application.dto.AuthResponseDto;

/**
 * Input port (use case) for authentication operations.
 */
public interface AuthServicePort {

    /**
     * Registers a new user and returns a JWT token.
     *
     * @param command the user registration command
     * @return an AuthResponseDto containing the JWT access token
     */
    AuthResponseDto register(RegisterUserCommand command);

    /**
     * Authenticates a user with email and password, then returns a JWT token.
     *
     * @param command the login command
     * @return an AuthResponseDto containing the JWT access token
     */
    AuthResponseDto login(LoginCommand command);

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param refreshToken the refresh token string
     * @return a new AuthResponseDto containing a new access token and refresh token
     */
    AuthResponseDto refreshToken(String refreshToken);
}
