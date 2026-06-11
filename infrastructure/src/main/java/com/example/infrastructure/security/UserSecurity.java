package com.example.infrastructure.security;

import com.example.application.port.out.UserPersistencePort;
import com.example.domain.model.User;
import com.example.infrastructure.adapter.out.persistence.repository.WatchlistJpaRepository;
import com.example.infrastructure.adapter.out.persistence.repository.AlertJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final UserPersistencePort userPersistencePort;
    private final WatchlistJpaRepository watchlistJpaRepository;
    private final AlertJpaRepository alertJpaRepository;

    public UserSecurity(UserPersistencePort userPersistencePort,
                        WatchlistJpaRepository watchlistJpaRepository,
                        AlertJpaRepository alertJpaRepository) {
        this.userPersistencePort = userPersistencePort;
        this.watchlistJpaRepository = watchlistJpaRepository;
        this.alertJpaRepository = alertJpaRepository;
    }

    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new RuntimeException("No authenticated user found");
        }
        return auth.getName();
    }

    public Long getCurrentUserId() {
        String email = getCurrentUserEmail();
        return userPersistencePort.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
    }

    public boolean isOwner(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return false;
        }
        return userPersistencePort.findById(userId)
                .map(User::getEmail)
                .filter(email -> email.equals(auth.getName()))
                .isPresent();
    }

    public boolean isWatchlistOwner(Long watchlistId) {
        Long currentUserId = getCurrentUserId();
        return watchlistJpaRepository.findById(watchlistId)
                .map(entity -> entity.getUser().getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean isAlertOwner(Long alertId) {
        Long currentUserId = getCurrentUserId();
        return alertJpaRepository.findById(alertId)
                .map(entity -> entity.getUser().getId().equals(currentUserId))
                .orElse(false);
    }
}
