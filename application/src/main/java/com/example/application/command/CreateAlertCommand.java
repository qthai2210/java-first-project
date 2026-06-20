package com.example.application.command;

import java.math.BigDecimal;

public record CreateAlertCommand(
        String symbol,
        String conditionType,
        String comparisonOperator,
        BigDecimal thresholdValue
) {
}
