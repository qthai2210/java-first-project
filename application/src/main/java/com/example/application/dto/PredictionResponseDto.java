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
public class PredictionResponseDto {
    private Long id;
    private String stockSymbol;
    private String stockName;
    private BigDecimal predictedPrice;
    private BigDecimal confidence;
    private int horizonDays;
    private String modelVersion;
    private LocalDateTime predictedAt;
}
