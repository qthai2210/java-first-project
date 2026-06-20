package com.example.infrastructure.adapter.in.web;

import com.example.application.command.LoginCommand;
import com.example.application.command.RegisterUserCommand;
import com.example.infrastructure.adapter.in.web.dto.AuthRequestDto;
import com.example.application.dto.AuthResponseDto;
import com.example.infrastructure.adapter.in.web.dto.TokenRefreshRequestDto;
import com.example.infrastructure.adapter.in.web.dto.UserRequestDto;
import com.example.application.port.in.AuthServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public authentication endpoints — no JWT token required.
 * These are whitelisted in SecurityConfig.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Public endpoints for user registration and login")
public class AuthController {

    private final AuthServicePort authServicePort;

    public AuthController(AuthServicePort authServicePort) {
        this.authServicePort = authServicePort;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user account", description = "Creates a new user with role USER and returns a JWT token")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody UserRequestDto request) {
        log.info("Request to register new user with email: {}", request.getEmail());
        RegisterUserCommand command = new RegisterUserCommand(request.getName(), request.getEmail(), request.getPassword());
        AuthResponseDto response = authServicePort.register(command);
        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password", description = "Authenticates user credentials and returns a JWT token")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        log.info("Login attempt for email: {}", request.getEmail());
        LoginCommand command = new LoginCommand(request.getEmail(), request.getPassword());
        AuthResponseDto response = authServicePort.login(command);
        log.info("Login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Uses a refresh token to generate a new access token and a new refresh token (rotation)")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody TokenRefreshRequestDto request) {
        log.info("Refresh token request initiated");
        AuthResponseDto response = authServicePort.refreshToken(request.getRefreshToken());
        log.info("Token refreshed successfully");
        return ResponseEntity.ok(response);
    }
}
