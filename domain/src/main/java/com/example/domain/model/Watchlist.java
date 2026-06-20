package com.example.domain.model;

import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
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

    public Watchlist addStock(Stock stock, String notes, BigDecimal targetPrice, BigDecimal stopLoss) {
        String symbol = stock.getSymbol();
        if (containsStock(symbol)) {
            throw new DomainException("Stock " + symbol + " is already in the watchlist");
        }

        WatchlistStock newStock = WatchlistStock.builder()
                .stock(stock)
                .addedAt(LocalDateTime.now())
                .notes(notes)
                .targetPrice(targetPrice)
                .stopLoss(stopLoss)
                .build();

        List<WatchlistStock> updatedStocks = new ArrayList<>(safeStocks());
        updatedStocks.add(newStock);

        return toBuilder()
                .stocks(updatedStocks)
                .build();
    }

    public Watchlist removeStock(String symbol) {
        if (!containsStock(symbol)) {
            throw new ResourceNotFoundException("Stock " + symbol + " not found in this watchlist");
        }

        List<WatchlistStock> updatedStocks = safeStocks().stream()
                .filter(watchlistStock -> !watchlistStock.hasSymbol(symbol))
                .toList();

        return toBuilder()
                .stocks(updatedStocks)
                .build();
    }

    public Watchlist updateStock(String symbol, String notes, BigDecimal targetPrice, BigDecimal stopLoss) {
        if (!containsStock(symbol)) {
            throw new ResourceNotFoundException("Stock " + symbol + " not found in this watchlist");
        }

        List<WatchlistStock> updatedStocks = safeStocks().stream()
                .map(watchlistStock -> watchlistStock.hasSymbol(symbol)
                        ? watchlistStock.updateTracking(notes, targetPrice, stopLoss)
                        : watchlistStock)
                .toList();

        return toBuilder()
                .stocks(updatedStocks)
                .build();
    }

    private boolean containsStock(String symbol) {
        return safeStocks().stream().anyMatch(watchlistStock -> watchlistStock.hasSymbol(symbol));
    }

    private List<WatchlistStock> safeStocks() {
        return stocks == null ? List.of() : stocks;
    }
}
