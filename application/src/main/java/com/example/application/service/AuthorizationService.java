package com.example.application.service;

import com.example.application.port.in.AuthorizationServicePort;
import com.example.application.port.out.AlertPersistencePort;
import com.example.application.port.out.UserPersistencePort;
import com.example.application.port.out.WatchlistPersistencePort;
import com.example.domain.exception.ResourceNotFoundException;
import com.example.domain.model.User;

public class AuthorizationService implements AuthorizationServicePort {

    private final UserPersistencePort userPersistencePort;
    private final WatchlistPersistencePort watchlistPersistencePort;
    private final AlertPersistencePort alertPersistencePort;

    public AuthorizationService(UserPersistencePort userPersistencePort,
                                WatchlistPersistencePort watchlistPersistencePort,
                                AlertPersistencePort alertPersistencePort) {
        this.userPersistencePort = userPersistencePort;
        this.watchlistPersistencePort = watchlistPersistencePort;
        this.alertPersistencePort = alertPersistencePort;
    }

    @Override
    public Long getUserIdByEmail(String email) {
        return userPersistencePort.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
    }

    @Override
    public boolean isUserOwner(String email, Long userId) {
        return userPersistencePort.findById(userId)
                .map(User::getEmail)
                .filter(email::equals)
                .isPresent();
    }

    @Override
    public boolean isWatchlistOwner(Long userId, Long watchlistId) {
        return watchlistPersistencePort.findById(watchlistId)
                .map(watchlist -> watchlist.getUser().getId().equals(userId))
                .orElse(false);
    }

    @Override
    public boolean isAlertOwner(Long userId, Long alertId) {
        return alertPersistencePort.findById(alertId)
                .map(alert -> alert.getUser().getId().equals(userId))
                .orElse(false);
    }
}
