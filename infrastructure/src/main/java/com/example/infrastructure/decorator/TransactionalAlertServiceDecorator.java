package com.example.infrastructure.decorator;

import com.example.application.dto.AlertRequestDto;
import com.example.application.port.in.AlertServicePort;
import com.example.domain.model.Alert;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class TransactionalAlertServiceDecorator implements AlertServicePort {

    private final AlertServicePort delegate;

    public TransactionalAlertServiceDecorator(AlertServicePort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public Alert createAlert(Long userId, AlertRequestDto dto) {
        return delegate.createAlert(userId, dto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Alert> getUserAlerts(Long userId) {
        return delegate.getUserAlerts(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Alert getAlertById(Long userId, Long alertId) {
        return delegate.getAlertById(userId, alertId);
    }

    @Override
    @Transactional
    public Alert updateAlert(Long userId, Long alertId, AlertRequestDto dto) {
        return delegate.updateAlert(userId, alertId, dto);
    }

    @Override
    @Transactional
    public void toggleAlert(Long userId, Long alertId, boolean enabled) {
        delegate.toggleAlert(userId, alertId, enabled);
    }

    @Override
    @Transactional
    public void deleteAlert(Long userId, Long alertId) {
        delegate.deleteAlert(userId, alertId);
    }

    @Override
    @Transactional
    public void checkAlerts() {
        delegate.checkAlerts();
    }
}
