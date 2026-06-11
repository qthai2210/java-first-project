package com.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Watchlist {
    @EqualsAndHashCode.Include
    private Long id;
    
    private String name;
    private User user;
    
    @Builder.Default
    private List<WatchlistStock> stocks = new ArrayList<>();
    
    private LocalDateTime createdAt;
}
