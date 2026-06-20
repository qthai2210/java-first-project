package com.example.infrastructure.security;

import com.example.application.port.in.AuthorizationServicePort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final AuthorizationServicePort authorizationServicePort;

    public UserSecurity(AuthorizationServicePort authorizationServicePort) {
        this.authorizationServicePort = authorizationServicePort;
    }

    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new RuntimeException("No authenticated user found");
        }
        return auth.getName();
    }

    public Long getCurrentUserId() {
        return authorizationServicePort.getUserIdByEmail(getCurrentUserEmail());
    }

    public boolean isOwner(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return false;
        }
        return authorizationServicePort.isUserOwner(auth.getName(), userId);
    }

    public boolean isWatchlistOwner(Long watchlistId) {
        return authorizationServicePort.isWatchlistOwner(getCurrentUserId(), watchlistId);
    }

    public boolean isAlertOwner(Long alertId) {
        return authorizationServicePort.isAlertOwner(getCurrentUserId(), alertId);
    }
}
