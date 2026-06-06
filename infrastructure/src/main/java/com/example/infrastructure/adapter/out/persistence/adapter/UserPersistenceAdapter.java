package com.example.infrastructure.adapter.out.persistence.adapter;

import com.example.application.port.out.UserPersistencePort;
import com.example.domain.model.User;
import com.example.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.example.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserJpaRepository userJpaRepository;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapToJpaEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    private UserJpaEntity mapToJpaEntity(User domain) {
        return UserJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    private User mapToDomain(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
