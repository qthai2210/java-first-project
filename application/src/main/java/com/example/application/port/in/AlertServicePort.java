package com.example.application.port.in;

import com.example.application.command.CreateAlertCommand;
import com.example.application.command.UpdateAlertCommand;
import com.example.domain.model.Alert;

import java.util.List;

public interface AlertServicePort {
    Alert createAlert(Long userId, CreateAlertCommand command);
    List<Alert> getUserAlerts(Long userId);
    Alert getAlertById(Long userId, Long alertId);
    Alert updateAlert(Long userId, Long alertId, UpdateAlertCommand command);
    void toggleAlert(Long userId, Long alertId, boolean enabled);
    void deleteAlert(Long userId, Long alertId);
    void checkAlerts();
}
