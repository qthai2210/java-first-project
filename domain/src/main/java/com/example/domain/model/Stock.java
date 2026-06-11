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
public class Stock {
    @EqualsAndHashCode.Include
    private Long id;
    
    @EqualsAndHashCode.Include
    private String symbol;
    
    private String name;
    private String exchange;
    private String sector;
    private BigDecimal marketCap;
    private String currency;
    private BigDecimal currentPrice;
    private LocalDateTime lastPriceUpdate;
    
    @Builder.Default
    private boolean active = true;
    
    private LocalDateTime createdAt;
}
