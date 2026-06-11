package com.example.application.port.out;

import com.example.domain.model.Alert;

import java.util.List;
import java.util.Optional;

public interface AlertPersistencePort {
    Alert save(Alert alert);
    Optional<Alert> findById(Long id);
    List<Alert> findByUserId(Long userId);
    List<Alert> findActiveAlertsByStockId(Long stockId);
    boolean existsById(Long id);
    void deleteById(Long id);
}
