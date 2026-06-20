package com.example.application.port.in;

public interface AuthorizationServicePort {
    Long getUserIdByEmail(String email);

    boolean isUserOwner(String email, Long userId);

    boolean isWatchlistOwner(Long userId, Long watchlistId);

    boolean isAlertOwner(Long userId, Long alertId);
}
