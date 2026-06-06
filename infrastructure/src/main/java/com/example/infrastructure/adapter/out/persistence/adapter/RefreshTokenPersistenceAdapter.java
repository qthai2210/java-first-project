package com.example.infrastructure.adapter.out.persistence.adapter;

import com.example.application.port.out.RefreshTokenPersistencePort;
import com.example.domain.model.RefreshToken;
import com.example.infrastructure.adapter.out.persistence.entity.RefreshTokenJpaEntity;
import com.example.infrastructure.adapter.out.persistence.repository.RefreshTokenJpaRepository;
import com.example.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.example.infrastructure.mapper.RefreshTokenMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenPersistencePort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    public RefreshTokenPersistenceAdapter(RefreshTokenJpaRepository refreshTokenJpaRepository,
                                           UserJpaRepository userJpaRepository,
                                           RefreshTokenMapper refreshTokenMapper) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.refreshTokenMapper = refreshTokenMapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = refreshTokenMapper.mapToJpaEntity(refreshToken);
        RefreshTokenJpaEntity savedEntity = refreshTokenJpaRepository.save(entity);
        return refreshTokenMapper.mapToDomain(savedEntity);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenJpaRepository.findByToken(token)
                .map(refreshTokenMapper::mapToDomain);
    }

    @Override
    @Transactional
    public void deleteByUser(Long userId) {
        userJpaRepository.findById(userId).ifPresent(userEntity -> {
            refreshTokenJpaRepository.deleteByUser(userEntity);
        });
    }
}
