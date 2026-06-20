package com.example.infrastructure.decorator;

import com.example.application.command.LoginCommand;
import com.example.application.command.RegisterUserCommand;
import com.example.application.dto.AuthResponseDto;
import com.example.application.port.in.AuthServicePort;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalAuthServiceDecorator implements AuthServicePort {

    private final AuthServicePort delegate;

    public TransactionalAuthServiceDecorator(AuthServicePort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public AuthResponseDto register(RegisterUserCommand command) {
        return delegate.register(command);
    }

    @Override
    @Transactional
    public AuthResponseDto login(LoginCommand command) {
        return delegate.login(command);
    }

    @Override
    @Transactional
    public AuthResponseDto refreshToken(String refreshToken) {
        return delegate.refreshToken(refreshToken);
    }
}
