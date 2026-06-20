package com.example.application.service;

import com.example.application.command.AddWatchlistStockCommand;
import com.example.application.command.UpdateWatchlistStockCommand;
import com.example.application.port.in.WatchlistServicePort;
import com.example.application.port.out.StockPersistencePort;
import com.example.application.port.out.UserPersistencePort;
import com.example.application.port.out.WatchlistPersistencePort;
import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResourceNotFoundException;
import com.example.domain.model.Stock;
import com.example.domain.model.User;
import com.example.domain.model.Watchlist;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WatchlistService implements WatchlistServicePort {

    private final WatchlistPersistencePort watchlistPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final StockPersistencePort stockPersistencePort;

    public WatchlistService(WatchlistPersistencePort watchlistPersistencePort,
                            UserPersistencePort userPersistencePort,
                            StockPersistencePort stockPersistencePort) {
        this.watchlistPersistencePort = watchlistPersistencePort;
        this.userPersistencePort = userPersistencePort;
        this.stockPersistencePort = stockPersistencePort;
    }

    @Override
    public Watchlist createWatchlist(Long userId, Watchlist watchlist) {
        log.debug("Creating watchlist for user: {}, name: {}", userId, watchlist.getName());
        User user = userPersistencePort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        if (watchlistPersistencePort.existsByUserIdAndName(userId, watchlist.getName())) {
            throw new DomainException("Watchlist with name '" + watchlist.getName() + "' already exists");
        }

        Watchlist watchlistToSave = watchlist.toBuilder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .stocks(new ArrayList<>())
                .build();

        return watchlistPersistencePort.save(watchlistToSave);
    }

    @Override
    public Watchlist getWatchlistById(Long userId, Long watchlistId) {
        log.debug("Fetching watchlist: {} for user: {}", watchlistId, userId);
        Watchlist watchlist = watchlistPersistencePort.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist with ID " + watchlistId + " not found"));

        if (!watchlist.getUser().getId().equals(userId)) {
            throw new DomainException("Access denied to watchlist " + watchlistId);
        }

        return watchlist;
    }

    @Override
    public List<Watchlist> getUserWatchlists(Long userId) {
        log.debug("Fetching all watchlists for user: {}", userId);
        return watchlistPersistencePort.findByUserId(userId);
    }

    @Override
    public Watchlist addStockToWatchlist(Long userId, Long watchlistId, AddWatchlistStockCommand command) {
        log.debug("Adding stock {} to watchlist: {}", command.symbol(), watchlistId);
        Watchlist watchlist = getWatchlistById(userId, watchlistId);
        Stock stock = stockPersistencePort.findBySymbol(command.symbol().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock with symbol " + command.symbol() + " not found"));

        Watchlist updatedWatchlist = watchlist.addStock(
                stock,
                command.notes(),
                command.targetPrice(),
                command.stopLoss()
        );

        return watchlistPersistencePort.save(updatedWatchlist);
    }

    @Override
    public Watchlist removeStockFromWatchlist(Long userId, Long watchlistId, String symbol) {
        log.debug("Removing stock {} from watchlist: {}", symbol, watchlistId);
        Watchlist watchlist = getWatchlistById(userId, watchlistId);

        Watchlist updatedWatchlist = watchlist.removeStock(symbol);

        return watchlistPersistencePort.save(updatedWatchlist);
    }

    @Override
    public Watchlist updateWatchlistStock(Long userId, Long watchlistId, UpdateWatchlistStockCommand command) {
        log.debug("Updating stock {} details in watchlist: {}", command.symbol(), watchlistId);
        Watchlist watchlist = getWatchlistById(userId, watchlistId);

        Watchlist updatedWatchlist = watchlist.updateStock(
                command.symbol(),
                command.notes(),
                command.targetPrice(),
                command.stopLoss()
        );

        return watchlistPersistencePort.save(updatedWatchlist);
    }

    @Override
    public void deleteWatchlist(Long userId, Long watchlistId) {
        log.debug("Deleting watchlist: {} for user: {}", watchlistId, userId);
        Watchlist watchlist = getWatchlistById(userId, watchlistId);
        watchlistPersistencePort.deleteById(watchlist.getId());
    }
}
