package com.example.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class StockRequestDto {
    @NotBlank(message = "Stock symbol is required")
    @Size(max = 20, message = "Symbol must be less than 20 characters")
    private String symbol;

    @NotBlank(message = "Stock name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    private String exchange;
    private String sector;
    private BigDecimal marketCap;
    
    @Builder.Default
    private String currency = "USD";
}
