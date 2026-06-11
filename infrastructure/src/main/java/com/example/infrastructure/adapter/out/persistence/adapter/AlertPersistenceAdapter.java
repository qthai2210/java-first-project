package com.example.infrastructure.adapter.out.persistence.adapter;

import com.example.application.port.out.AlertPersistencePort;
import com.example.domain.model.Alert;
import com.example.infrastructure.adapter.out.persistence.entity.AlertJpaEntity;
import com.example.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.example.infrastructure.adapter.out.persistence.repository.AlertJpaRepository;
import com.example.infrastructure.mapper.AlertMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AlertPersistenceAdapter implements AlertPersistencePort {

    private final AlertJpaRepository alertJpaRepository;
    private final AlertMapper alertMapper;

    public AlertPersistenceAdapter(AlertJpaRepository alertJpaRepository, AlertMapper alertMapper) {
        this.alertJpaRepository = alertJpaRepository;
        this.alertMapper = alertMapper;
    }

    @Override
    public Alert save(Alert alert) {
        AlertJpaEntity entity = alertMapper.mapToJpaEntity(alert);
        if (alert.getUser() != null) {
            entity.setUser(UserJpaEntity.builder().id(alert.getUser().getId()).build());
        }
        AlertJpaEntity saved = alertJpaRepository.save(entity);
        return alertMapper.mapToDomain(saved);
    }

    @Override
    public Optional<Alert> findById(Long id) {
        return alertJpaRepository.findById(id).map(alertMapper::mapToDomain);
    }

    @Override
    public List<Alert> findByUserId(Long userId) {
        return alertJpaRepository.findByUserId(userId).stream()
                .map(alertMapper::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alert> findActiveAlertsByStockId(Long stockId) {
        return alertJpaRepository.findByStockIdAndEnabledTrue(stockId).stream()
                .map(alertMapper::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return alertJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        alertJpaRepository.deleteById(id);
    }
}
