package com.example.infrastructure.security;

import com.example.application.port.out.UserPersistencePort;
import com.example.domain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final UserPersistencePort userPersistencePort;

    public UserSecurity(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
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
}
