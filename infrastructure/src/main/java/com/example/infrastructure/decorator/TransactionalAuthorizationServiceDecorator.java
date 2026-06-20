package com.example.infrastructure.decorator;

import com.example.application.port.in.AuthorizationServicePort;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalAuthorizationServiceDecorator implements AuthorizationServicePort {

    private final AuthorizationServicePort delegate;

    public TransactionalAuthorizationServiceDecorator(AuthorizationServicePort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        return delegate.getUserIdByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserOwner(String email, Long userId) {
        return delegate.isUserOwner(email, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isWatchlistOwner(Long userId, Long watchlistId) {
        return delegate.isWatchlistOwner(userId, watchlistId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAlertOwner(Long userId, Long alertId) {
        return delegate.isAlertOwner(userId, alertId);
    }
}
