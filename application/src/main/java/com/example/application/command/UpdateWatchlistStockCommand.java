package com.example.application.command;

import java.math.BigDecimal;

public record UpdateWatchlistStockCommand(
        String symbol,
        String notes,
        BigDecimal targetPrice,
        BigDecimal stopLoss
) {
}
