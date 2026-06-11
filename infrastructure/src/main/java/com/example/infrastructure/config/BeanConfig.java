package com.example.infrastructure.config;

import com.example.application.port.in.AuthServicePort;
import com.example.application.port.in.StockServicePort;
import com.example.application.port.in.UserServicePort;
import com.example.application.port.in.WatchlistServicePort;
import com.example.application.port.in.PredictionServicePort;
import com.example.application.port.in.AlertServicePort;
import com.example.application.port.out.*;
import com.example.application.service.AuthService;
import com.example.application.service.StockService;
import com.example.application.service.UserService;
import com.example.application.service.WatchlistService;
import com.example.application.service.PredictionService;
import com.example.application.service.AlertService;
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
    public StockServicePort stockServicePort(StockPersistencePort stockPersistencePort) {
        return new StockService(stockPersistencePort);
    }

    @Bean
    public WatchlistServicePort watchlistServicePort(WatchlistPersistencePort watchlistPersistencePort,
                                                     UserPersistencePort userPersistencePort,
                                                     StockPersistencePort stockPersistencePort) {
        return new WatchlistService(watchlistPersistencePort, userPersistencePort, stockPersistencePort);
    }

    @Bean
    public PredictionServicePort predictionServicePort(PredictionPersistencePort predictionPersistencePort,
                                                       StockPersistencePort stockPersistencePort) {
        return new PredictionService(predictionPersistencePort, stockPersistencePort);
    }

    @Bean
    public AlertServicePort alertServicePort(AlertPersistencePort alertPersistencePort,
                                             UserPersistencePort userPersistencePort,
                                             StockPersistencePort stockPersistencePort) {
        return new AlertService(alertPersistencePort, userPersistencePort, stockPersistencePort);
    }

    @Bean
    public AuthServicePort authServicePort(UserPersistencePort userPersistencePort,
                                           PasswordEncoderPort passwordEncoderPort,
                                           JwtServicePort jwtServicePort,
                                           RefreshTokenPersistencePort refreshTokenPersistencePort) {
        return new AuthService(userPersistencePort, passwordEncoderPort, jwtServicePort, refreshTokenPersistencePort, refreshExpirationMs);
    }
}
