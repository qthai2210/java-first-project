package com.example.infrastructure.config;

import com.example.application.port.in.UserServicePort;
import com.example.application.port.out.UserPersistencePort;
import com.example.application.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public UserServicePort userServicePort(UserPersistencePort userPersistencePort) {
        return new UserService(userPersistencePort);
    }
}
