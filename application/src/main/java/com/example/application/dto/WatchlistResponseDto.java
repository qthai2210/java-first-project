package com.example.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistResponseDto {
    private Long id;
    private String name;
    private Long userId;
    private List<WatchlistStockResponseDto> stocks;
    private LocalDateTime createdAt;
}
