package com.example.infrastructure.config;

import com.example.application.port.in.AuthorizationServicePort;
import com.example.application.port.in.AuthServicePort;
import com.example.application.port.in.StockServicePort;
import com.example.application.port.in.UserServicePort;
import com.example.application.port.in.WatchlistServicePort;
import com.example.application.port.in.PredictionServicePort;
import com.example.application.port.in.AlertServicePort;
import com.example.application.port.out.*;
import com.example.application.service.AuthorizationService;
import com.example.application.service.AuthService;
import com.example.application.service.StockService;
import com.example.application.service.UserService;
import com.example.application.service.WatchlistService;
import com.example.application.service.PredictionService;
import com.example.application.service.AlertService;
import com.example.infrastructure.decorator.TransactionalAlertServiceDecorator;
import com.example.infrastructure.decorator.TransactionalAuthorizationServiceDecorator;
import com.example.infrastructure.decorator.TransactionalAuthServiceDecorator;
import com.example.infrastructure.decorator.TransactionalPredictionServiceDecorator;
import com.example.infrastructure.decorator.TransactionalStockServiceDecorator;
import com.example.infrastructure.decorator.TransactionalUserServiceDecorator;
import com.example.infrastructure.decorator.TransactionalWatchlistServiceDecorator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Bean
    public AuthorizationServicePort authorizationServicePort(UserPersistencePort userPersistencePort,
                                                             WatchlistPersistencePort watchlistPersistencePort,
                                                             AlertPersistencePort alertPersistencePort) {
        return new TransactionalAuthorizationServiceDecorator(
                new AuthorizationService(userPersistencePort, watchlistPersistencePort, alertPersistencePort)
        );
    }

    @Bean
    public UserServicePort userServicePort(UserPersistencePort userPersistencePort, PasswordEncoderPort passwordEncoderPort) {
        return new TransactionalUserServiceDecorator(
                new UserService(userPersistencePort, passwordEncoderPort)
        );
    }

    @Bean
    public StockServicePort stockServicePort(StockPersistencePort stockPersistencePort) {
        return new TransactionalStockServiceDecorator(
                new StockService(stockPersistencePort)
        );
    }

    @Bean
    public WatchlistServicePort watchlistServicePort(WatchlistPersistencePort watchlistPersistencePort,
                                                     UserPersistencePort userPersistencePort,
                                                     StockPersistencePort stockPersistencePort) {
        return new TransactionalWatchlistServiceDecorator(
                new WatchlistService(watchlistPersistencePort, userPersistencePort, stockPersistencePort)
        );
    }

    @Bean
    public PredictionServicePort predictionServicePort(PredictionPersistencePort predictionPersistencePort,
                                                       StockPersistencePort stockPersistencePort) {
        return new TransactionalPredictionServiceDecorator(
                new PredictionService(predictionPersistencePort, stockPersistencePort)
        );
    }

    @Bean
    public AlertServicePort alertServicePort(AlertPersistencePort alertPersistencePort,
                                             UserPersistencePort userPersistencePort,
                                             StockPersistencePort stockPersistencePort) {
        return new TransactionalAlertServiceDecorator(
                new AlertService(alertPersistencePort, userPersistencePort, stockPersistencePort)
        );
    }

    @Bean
    public AuthServicePort authServicePort(UserPersistencePort userPersistencePort,
                                           PasswordEncoderPort passwordEncoderPort,
                                           JwtServicePort jwtServicePort,
                                           RefreshTokenPersistencePort refreshTokenPersistencePort) {
        return new TransactionalAuthServiceDecorator(
                new AuthService(userPersistencePort, passwordEncoderPort, jwtServicePort, refreshTokenPersistencePort, refreshExpirationMs)
        );
    }
}
