package com.example.infrastructure.config;

import com.example.application.port.in.AuthServicePort;
import com.example.application.port.in.UserServicePort;
import com.example.application.port.out.JwtServicePort;
import com.example.application.port.out.PasswordEncoderPort;
import com.example.application.port.out.UserPersistencePort;
import com.example.application.service.AuthService;
import com.example.application.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public UserServicePort userServicePort(UserPersistencePort userPersistencePort, PasswordEncoderPort passwordEncoderPort) {
        return new UserService(userPersistencePort, passwordEncoderPort);
    }

    @Bean
    public AuthServicePort authServicePort(UserPersistencePort userPersistencePort,
                                           PasswordEncoderPort passwordEncoderPort,
                                           JwtServicePort jwtServicePort) {
        return new AuthService(userPersistencePort, passwordEncoderPort, jwtServicePort);
    }
}
