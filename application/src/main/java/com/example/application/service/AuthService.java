package com.example.application.service;

import com.example.application.dto.AuthRequestDto;
import com.example.application.dto.AuthResponseDto;
import com.example.application.dto.UserRequestDto;
import com.example.application.port.in.AuthServicePort;
import com.example.application.port.out.JwtServicePort;
import com.example.application.port.out.PasswordEncoderPort;
import com.example.application.port.out.UserPersistencePort;
import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResourceNotFoundException;
import com.example.domain.model.Role;
import com.example.domain.model.User;

import java.time.LocalDateTime;

/**
 * Application service implementing authentication use cases.
 * Has no dependency on Spring Security or JJWT — only uses ports.
 */
public class AuthService implements AuthServicePort {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtServicePort jwtServicePort;

    public AuthService(UserPersistencePort userPersistencePort,
                       PasswordEncoderPort passwordEncoderPort,
                       JwtServicePort jwtServicePort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtServicePort = jwtServicePort;
    }

    @Override
    public AuthResponseDto register(UserRequestDto request) {
        // Business rule: email must be unique
        if (userPersistencePort.existsByEmail(request.getEmail())) {
            throw new DomainException("Email is already registered: " + request.getEmail());
        }

        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoderPort.encode(request.getPassword()))
                .role(Role.USER)  // Default role is USER
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userPersistencePort.save(newUser);
        String token = jwtServicePort.generateToken(savedUser);

        return AuthResponseDto.builder()
                .accessToken(token)
                .build();
    }

    @Override
    public AuthResponseDto login(AuthRequestDto request) {
        User user = userPersistencePort.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoderPort.matches(request.getPassword(), user.getPassword())) {
            throw new DomainException("Invalid email or password");
        }

        String token = jwtServicePort.generateToken(user);

        return AuthResponseDto.builder()
                .accessToken(token)
                .build();
    }
}
