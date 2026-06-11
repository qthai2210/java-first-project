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
public class AlertResponseDto {
    private Long id;
    private String stockSymbol;
    private String stockName;
    private String conditionType;
    private String comparisonOperator;
    private BigDecimal thresholdValue;
    private boolean enabled;
    private LocalDateTime triggeredAt;
    private LocalDateTime createdAt;
}
