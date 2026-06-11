package com.example.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AlertRequestDto {
    @NotBlank(message = "Stock symbol is required")
    private String symbol;

    @NotBlank(message = "Condition type is required")
    private String conditionType;

    @NotBlank(message = "Comparison operator is required")
    private String comparisonOperator;

    @NotNull(message = "Threshold value is required")
    private BigDecimal thresholdValue;
}
