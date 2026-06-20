package com.example.application.command;

import java.math.BigDecimal;

public record UpdateAlertCommand(
        String symbol,
        String conditionType,
        String comparisonOperator,
        BigDecimal thresholdValue
) {
}
