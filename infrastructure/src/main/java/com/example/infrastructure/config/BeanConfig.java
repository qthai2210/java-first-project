package com.example.infrastructure.config;

import com.example.application.port.in.AuthServicePort;
import com.example.application.port.in.UserServicePort;
import com.example.application.port.out.JwtServicePort;
import com.example.application.port.out.PasswordEncoderPort;
import com.example.application.port.out.UserPersistencePort;
import com.example.application.port.out.RefreshTokenPersistencePort;
import com.example.application.service.AuthService;
import com.example.application.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Bean
    public UserServicePort userServicePort(UserPersistencePort userPersistencePort, PasswordEncoderPort passwordEncoderPort) {
        return new UserService(userPersistencePort, passwordEncoderPort);
    }

    @Bean
    public AuthServicePort authServicePort(UserPersistencePort userPersistencePort,
                                           PasswordEncoderPort passwordEncoderPort,
                                           JwtServicePort jwtServicePort,
                                           RefreshTokenPersistencePort refreshTokenPersistencePort) {
        return new AuthService(userPersistencePort, passwordEncoderPort, jwtServicePort, refreshTokenPersistencePort, refreshExpirationMs);
    }
}
