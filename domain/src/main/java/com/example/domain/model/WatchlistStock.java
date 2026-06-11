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
}
