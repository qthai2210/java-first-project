package com.example.application.command;

import java.math.BigDecimal;

public record AddWatchlistStockCommand(
        String symbol,
        String notes,
        BigDecimal targetPrice,
        BigDecimal stopLoss
) {
}
