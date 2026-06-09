package com.example.application.service;

import com.example.application.dto.AuthRequestDto;
import com.example.application.dto.AuthResponseDto;
import com.example.application.dto.UserRequestDto;
import com.example.application.port.in.AuthServicePort;
import com.example.application.port.out.JwtServicePort;
import com.example.application.port.out.PasswordEncoderPort;
import com.example.application.port.out.RefreshTokenPersistencePort;
import com.example.application.port.out.UserPersistencePort;
import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResourceNotFoundException;
import com.example.domain.model.RefreshToken;
import com.example.domain.model.Role;
import com.example.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Application service implementing authentication use cases.
 * Has no dependency on Spring Security or JJWT — only uses ports.
 */
public class AuthService implements AuthServicePort {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtServicePort jwtServicePort;
    private final RefreshTokenPersistencePort refreshTokenPersistencePort;
    private final long refreshExpirationMs;

    public AuthService(UserPersistencePort userPersistencePort,
                       PasswordEncoderPort passwordEncoderPort,
                       JwtServicePort jwtServicePort,
                       RefreshTokenPersistencePort refreshTokenPersistencePort,
                       long refreshExpirationMs) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtServicePort = jwtServicePort;
        this.refreshTokenPersistencePort = refreshTokenPersistencePort;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    @Transactional
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
        RefreshToken refreshToken = createRefreshToken(savedUser);

        return AuthResponseDto.builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDto login(AuthRequestDto request) {
        User user = userPersistencePort.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoderPort.matches(request.getPassword(), user.getPassword())) {
            throw new DomainException("Invalid email or password");
        }

        String token = jwtServicePort.generateToken(user);
        
        // Delete old refresh tokens for this user first
        refreshTokenPersistencePort.deleteByUser(user.getId());
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDto refreshToken(String tokenStr) {
        RefreshToken refreshToken = refreshTokenPersistencePort.findByToken(tokenStr)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (refreshToken.isExpired()) {
            refreshTokenPersistencePort.deleteByUser(refreshToken.getUser().getId());
            throw new DomainException("Refresh token was expired. Please make a new signin request");
        }

        User user = refreshToken.getUser();
        
        // Rotate: delete the old refresh token, then create a new one
        refreshTokenPersistencePort.deleteByUser(user.getId());
        
        String newAccessToken = jwtServicePort.generateToken(user);
        RefreshToken newRefreshToken = createRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(java.util.UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        return refreshTokenPersistencePort.save(refreshToken);
    }
}
