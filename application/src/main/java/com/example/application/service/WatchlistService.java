package com.example.application.service;

import com.example.application.dto.WatchlistStockRequestDto;
import com.example.application.port.in.WatchlistServicePort;
import com.example.application.port.out.StockPersistencePort;
import com.example.application.port.out.UserPersistencePort;
import com.example.application.port.out.WatchlistPersistencePort;
import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResourceNotFoundException;
import com.example.domain.model.Stock;
import com.example.domain.model.User;
import com.example.domain.model.Watchlist;
import com.example.domain.model.WatchlistStock;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<Watchlist> getUserWatchlists(Long userId) {
        log.debug("Fetching all watchlists for user: {}", userId);
        return watchlistPersistencePort.findByUserId(userId);
    }

    @Override
    @Transactional
    public Watchlist addStockToWatchlist(Long userId, Long watchlistId, WatchlistStockRequestDto dto) {
        log.debug("Adding stock {} to watchlist: {}", dto.getSymbol(), watchlistId);
        Watchlist watchlist = getWatchlistById(userId, watchlistId);
        Stock stock = stockPersistencePort.findBySymbol(dto.getSymbol().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock with symbol " + dto.getSymbol() + " not found"));

        boolean alreadyExists = watchlist.getStocks().stream()
                .anyMatch(ws -> ws.getStock().getSymbol().equalsIgnoreCase(dto.getSymbol()));

        if (alreadyExists) {
            throw new DomainException("Stock " + dto.getSymbol() + " is already in the watchlist");
        }

        WatchlistStock newStock = WatchlistStock.builder()
                .stock(stock)
                .addedAt(LocalDateTime.now())
                .notes(dto.getNotes())
                .targetPrice(dto.getTargetPrice())
                .stopLoss(dto.getStopLoss())
                .build();

        List<WatchlistStock> updatedStocks = new ArrayList<>(watchlist.getStocks());
        updatedStocks.add(newStock);

        Watchlist updatedWatchlist = watchlist.toBuilder()
                .stocks(updatedStocks)
                .build();

        return watchlistPersistencePort.save(updatedWatchlist);
    }

    @Override
    @Transactional
    public Watchlist removeStockFromWatchlist(Long userId, Long watchlistId, String symbol) {
        log.debug("Removing stock {} from watchlist: {}", symbol, watchlistId);
        Watchlist watchlist = getWatchlistById(userId, watchlistId);

        boolean exists = watchlist.getStocks().stream()
                .anyMatch(ws -> ws.getStock().getSymbol().equalsIgnoreCase(symbol));

        if (!exists) {
            throw new ResourceNotFoundException("Stock " + symbol + " not found in this watchlist");
        }

        List<WatchlistStock> updatedStocks = watchlist.getStocks().stream()
                .filter(ws -> !ws.getStock().getSymbol().equalsIgnoreCase(symbol))
                .toList();

        Watchlist updatedWatchlist = watchlist.toBuilder()
                .stocks(updatedStocks)
                .build();

        return watchlistPersistencePort.save(updatedWatchlist);
    }

    @Override
    @Transactional
    public Watchlist updateWatchlistStock(Long userId, Long watchlistId, WatchlistStockRequestDto dto) {
        log.debug("Updating stock {} details in watchlist: {}", dto.getSymbol(), watchlistId);
        Watchlist watchlist = getWatchlistById(userId, watchlistId);

        boolean exists = watchlist.getStocks().stream()
                .anyMatch(ws -> ws.getStock().getSymbol().equalsIgnoreCase(dto.getSymbol()));

        if (!exists) {
            throw new ResourceNotFoundException("Stock " + dto.getSymbol() + " not found in this watchlist");
        }

        List<WatchlistStock> updatedStocks = watchlist.getStocks().stream()
                .map(ws -> {
                    if (ws.getStock().getSymbol().equalsIgnoreCase(dto.getSymbol())) {
                        return ws.toBuilder()
                                .notes(dto.getNotes())
                                .targetPrice(dto.getTargetPrice())
                                .stopLoss(dto.getStopLoss())
                                .build();
                    }
                    return ws;
                })
                .toList();

        Watchlist updatedWatchlist = watchlist.toBuilder()
                .stocks(updatedStocks)
                .build();

        return watchlistPersistencePort.save(updatedWatchlist);
    }

    @Override
    @Transactional
    public void deleteWatchlist(Long userId, Long watchlistId) {
        log.debug("Deleting watchlist: {} for user: {}", watchlistId, userId);
        Watchlist watchlist = getWatchlistById(userId, watchlistId);
        watchlistPersistencePort.deleteById(watchlist.getId());
    }
}
