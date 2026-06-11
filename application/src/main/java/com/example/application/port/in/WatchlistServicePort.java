package com.example.application.port.in;

import com.example.application.dto.WatchlistStockRequestDto;
import com.example.domain.model.Watchlist;

import java.util.List;

public interface WatchlistServicePort {
    Watchlist createWatchlist(Long userId, Watchlist watchlist);

    Watchlist getWatchlistById(Long userId, Long watchlistId);

    List<Watchlist> getUserWatchlists(Long userId);

    Watchlist addStockToWatchlist(Long userId, Long watchlistId, WatchlistStockRequestDto dto);

    Watchlist removeStockFromWatchlist(Long userId, Long watchlistId, String symbol);

    Watchlist updateWatchlistStock(Long userId, Long watchlistId, WatchlistStockRequestDto dto);

    void deleteWatchlist(Long userId, Long watchlistId);
}
