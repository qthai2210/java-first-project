package com.example.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistStockRequestDto {
    @NotBlank(message = "Stock symbol is required")
    private String symbol;
    
    private String notes;
    private BigDecimal targetPrice;
    private BigDecimal stopLoss;
}
