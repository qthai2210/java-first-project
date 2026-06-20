package com.example.application.service;

import com.example.application.dto.AlertRequestDto;
import com.example.application.port.in.AlertServicePort;
import com.example.application.port.out.AlertPersistencePort;
import com.example.application.port.out.StockPersistencePort;
import com.example.application.port.out.UserPersistencePort;
import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResourceNotFoundException;
import com.example.domain.model.Alert;
import com.example.domain.model.AlertConditionType;
import com.example.domain.model.ComparisonOperator;
import com.example.domain.model.Stock;
import com.example.domain.model.User;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class AlertService implements AlertServicePort {

    private final AlertPersistencePort alertPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final StockPersistencePort stockPersistencePort;

    public AlertService(AlertPersistencePort alertPersistencePort,
                        UserPersistencePort userPersistencePort,
                        StockPersistencePort stockPersistencePort) {
        this.alertPersistencePort = alertPersistencePort;
        this.userPersistencePort = userPersistencePort;
        this.stockPersistencePort = stockPersistencePort;
    }

    @Override

    public Alert createAlert(Long userId, AlertRequestDto dto) {
        log.debug("Creating alert for user ID: {} on stock: {}", userId, dto.getSymbol());
        User user = userPersistencePort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        Stock stock = stockPersistencePort.findBySymbol(dto.getSymbol().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock with symbol " + dto.getSymbol() + " not found"));

        AlertConditionType conditionType;
        ComparisonOperator comparisonOperator;
        try {
            conditionType = AlertConditionType.valueOf(dto.getConditionType().toUpperCase());
            comparisonOperator = ComparisonOperator.valueOf(dto.getComparisonOperator().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid condition type or comparison operator");
        }

        Alert alert = Alert.builder()
                .user(user)
                .stock(stock)
                .conditionType(conditionType)
                .comparisonOperator(comparisonOperator)
                .thresholdValue(dto.getThresholdValue())
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        return alertPersistencePort.save(alert);
    }

    @Override
    public List<Alert> getUserAlerts(Long userId) {
        log.debug("Fetching all alerts for user ID: {}", userId);
        return alertPersistencePort.findByUserId(userId);
    }

    @Override
    public Alert getAlertById(Long userId, Long alertId) {
        log.debug("Fetching alert: {} for user: {}", alertId, userId);
        Alert alert = alertPersistencePort.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert with ID " + alertId + " not found"));

        if (!alert.getUser().getId().equals(userId)) {
            throw new DomainException("Access denied to alert " + alertId);
        }

        return alert;
    }

    @Override

    public Alert updateAlert(Long userId, Long alertId, AlertRequestDto dto) {
        log.debug("Updating alert ID: {}", alertId);
        Alert alert = getAlertById(userId, alertId);

        AlertConditionType conditionType;
        ComparisonOperator comparisonOperator;
        try {
            conditionType = AlertConditionType.valueOf(dto.getConditionType().toUpperCase());
            comparisonOperator = ComparisonOperator.valueOf(dto.getComparisonOperator().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid condition type or comparison operator");
        }

        Alert updated = alert.toBuilder()
                .conditionType(conditionType)
                .comparisonOperator(comparisonOperator)
                .thresholdValue(dto.getThresholdValue())
                .build();

        return alertPersistencePort.save(updated);
    }

    @Override

    public void toggleAlert(Long userId, Long alertId, boolean enabled) {
        log.debug("Toggling alert ID: {} to enabled: {}", alertId, enabled);
        Alert alert = getAlertById(userId, alertId);
        Alert updated = alert.toBuilder().enabled(enabled).build();
        alertPersistencePort.save(updated);
    }

    @Override

    public void deleteAlert(Long userId, Long alertId) {
        log.debug("Deleting alert ID: {} for user: {}", alertId, userId);
        Alert alert = getAlertById(userId, alertId);
        alertPersistencePort.deleteById(alert.getId());
    }

    @Override

    public void checkAlerts() {
        // Defer alert execution and market rule processing to Phase 7
        log.info("Alert evaluation execution triggered (Staged/Deferred for Phase 7 scheduler).");
    }
}
