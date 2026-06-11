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
public class StockResponseDto {
    private Long id;
    private String symbol;
    private String name;
    private String exchange;
    private String sector;
    private BigDecimal marketCap;
    private String currency;
    private BigDecimal currentPrice;
    private LocalDateTime lastPriceUpdate;
    private boolean active;
    private LocalDateTime createdAt;
}
