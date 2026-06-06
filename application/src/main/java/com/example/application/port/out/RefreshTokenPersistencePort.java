package com.example.application.port.out;

import com.example.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenPersistencePort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(Long userId);
}
