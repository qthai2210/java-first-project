package com.example.infrastructure.adapter.in.web;

import com.example.application.command.CreateAlertCommand;
import com.example.application.command.UpdateAlertCommand;
import com.example.infrastructure.adapter.in.web.dto.AlertRequestDto;
import com.example.application.dto.AlertResponseDto;
import com.example.application.port.in.AlertServicePort;
import com.example.domain.model.Alert;
import com.example.infrastructure.mapper.AlertMapper;
import com.example.infrastructure.security.UserSecurity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Alert Management", description = "Endpoints for managing user alert rules")
@SecurityRequirement(name = "bearerAuth")
public class AlertController {

    private final AlertServicePort alertServicePort;
    private final AlertMapper alertMapper;
    private final UserSecurity userSecurity;

    public AlertController(AlertServicePort alertServicePort,
                           AlertMapper alertMapper,
                           UserSecurity userSecurity) {
        this.alertServicePort = alertServicePort;
        this.alertMapper = alertMapper;
        this.userSecurity = userSecurity;
    }

    @PostMapping
    @Operation(summary = "Create a new price/volume alert rule")
    public ResponseEntity<AlertResponseDto> createAlert(@Valid @RequestBody AlertRequestDto request) {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to create alert for stock: {} and user ID: {}", request.getSymbol(), userId);
        CreateAlertCommand command = new CreateAlertCommand(
                request.getSymbol(),
                request.getConditionType(),
                request.getComparisonOperator(),
                request.getThresholdValue()
        );
        Alert created = alertServicePort.createAlert(userId, command);
        return new ResponseEntity<>(alertMapper.mapToDto(created), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all alerts of the current user")
    public ResponseEntity<List<AlertResponseDto>> getUserAlerts() {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to get all alerts for user ID: {}", userId);
        List<Alert> alerts = alertServicePort.getUserAlerts(userId);
        List<AlertResponseDto> response = alerts.stream()
                .map(alertMapper::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an alert by ID")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isAlertOwner(#id)")
    public ResponseEntity<AlertResponseDto> getAlertById(@PathVariable Long id) {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to get alert: {} for user ID: {}", id, userId);
        Alert alert = alertServicePort.getAlertById(userId, id);
        return ResponseEntity.ok(alertMapper.mapToDto(alert));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an alert by ID")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isAlertOwner(#id)")
    public ResponseEntity<AlertResponseDto> updateAlert(
            @PathVariable Long id,
            @Valid @RequestBody AlertRequestDto request) {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to update alert: {} for user ID: {}", id, userId);
        UpdateAlertCommand command = new UpdateAlertCommand(
                request.getSymbol(),
                request.getConditionType(),
                request.getComparisonOperator(),
                request.getThresholdValue()
        );
        Alert updated = alertServicePort.updateAlert(userId, id, command);
        return ResponseEntity.ok(alertMapper.mapToDto(updated));
    }

    @PutMapping("/{id}/toggle")
    @Operation(summary = "Enable or disable an alert")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isAlertOwner(#id)")
    public ResponseEntity<Void> toggleAlert(@PathVariable Long id, @RequestParam boolean enabled) {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to toggle alert: {} to enabled: {} for user ID: {}", id, enabled, userId);
        alertServicePort.toggleAlert(userId, id, enabled);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an alert by ID")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isAlertOwner(#id)")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to delete alert: {} for user ID: {}", id, userId);
        alertServicePort.deleteAlert(userId, id);
        return ResponseEntity.noContent().build();
    }
}
