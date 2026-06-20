package com.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WatchlistStock {
    private Stock stock;
    private LocalDateTime addedAt;
    private String notes;
    private BigDecimal targetPrice;
    private BigDecimal stopLoss;

    public boolean hasSymbol(String symbol) {
        return stock != null
                && stock.getSymbol() != null
                && stock.getSymbol().equalsIgnoreCase(symbol);
    }

    public WatchlistStock updateTracking(String notes, BigDecimal targetPrice, BigDecimal stopLoss) {
        return toBuilder()
                .notes(notes)
                .targetPrice(targetPrice)
                .stopLoss(stopLoss)
                .build();
    }
}
