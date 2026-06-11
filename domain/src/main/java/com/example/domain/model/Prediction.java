package com.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Prediction {
    @EqualsAndHashCode.Include
    private Long id;
    
    private Stock stock;
    private BigDecimal predictedPrice;
    private BigDecimal confidence;
    private int horizonDays;
    private String modelVersion;
    private String featuresJson;
    private LocalDateTime predictedAt;
}
