package com.example.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistStockResponseDto {
    private String symbol;
    private String name;
    private BigDecimal currentPrice;
    private LocalDateTime addedAt;
    private String notes;
    private BigDecimal targetPrice;
    private BigDecimal stopLoss;
}
