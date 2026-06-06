package com.example.infrastructure.adapter.in.web;

import com.example.application.dto.AuthRequestDto;
import com.example.application.dto.AuthResponseDto;
import com.example.application.dto.UserRequestDto;
import com.example.application.port.in.AuthServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public authentication endpoints — no JWT token required.
 * These are whitelisted in SecurityConfig.
 */
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
        return ResponseEntity.ok(authServicePort.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password", description = "Authenticates user credentials and returns a JWT token")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        return ResponseEntity.ok(authServicePort.login(request));
    }
}
