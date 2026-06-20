package com.example.infrastructure.decorator;

import com.example.application.dto.AuthRequestDto;
import com.example.application.dto.AuthResponseDto;
import com.example.application.dto.UserRequestDto;
import com.example.application.port.in.AuthServicePort;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalAuthServiceDecorator implements AuthServicePort {

    private final AuthServicePort delegate;

    public TransactionalAuthServiceDecorator(AuthServicePort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public AuthResponseDto register(UserRequestDto request) {
        return delegate.register(request);
    }

    @Override
    @Transactional
    public AuthResponseDto login(AuthRequestDto request) {
        return delegate.login(request);
    }

    @Override
    @Transactional
    public AuthResponseDto refreshToken(String refreshToken) {
        return delegate.refreshToken(refreshToken);
    }
}
