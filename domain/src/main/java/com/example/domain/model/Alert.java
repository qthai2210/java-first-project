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
public class Alert {
    @EqualsAndHashCode.Include
    private Long id;
    
    private User user;
    private Stock stock;
    private AlertConditionType conditionType;
    private ComparisonOperator comparisonOperator;
    private BigDecimal thresholdValue;
    
    @Builder.Default
    private boolean enabled = true;
    
    private LocalDateTime triggeredAt;
    private LocalDateTime createdAt;
}
