package com.example.infrastructure.decorator;

import com.example.application.command.AddWatchlistStockCommand;
import com.example.application.command.UpdateWatchlistStockCommand;
import com.example.application.port.in.WatchlistServicePort;
import com.example.domain.model.Watchlist;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class TransactionalWatchlistServiceDecorator implements WatchlistServicePort {

    private final WatchlistServicePort delegate;

    public TransactionalWatchlistServiceDecorator(WatchlistServicePort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public Watchlist createWatchlist(Long userId, Watchlist watchlist) {
        return delegate.createWatchlist(userId, watchlist);
    }

    @Override
    @Transactional(readOnly = true)
    public Watchlist getWatchlistById(Long userId, Long watchlistId) {
        return delegate.getWatchlistById(userId, watchlistId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Watchlist> getUserWatchlists(Long userId) {
        return delegate.getUserWatchlists(userId);
    }

    @Override
    @Transactional
    public Watchlist addStockToWatchlist(Long userId, Long watchlistId, AddWatchlistStockCommand command) {
        return delegate.addStockToWatchlist(userId, watchlistId, command);
    }

    @Override
    @Transactional
    public Watchlist removeStockFromWatchlist(Long userId, Long watchlistId, String symbol) {
        return delegate.removeStockFromWatchlist(userId, watchlistId, symbol);
    }

    @Override
    @Transactional
    public Watchlist updateWatchlistStock(Long userId, Long watchlistId, UpdateWatchlistStockCommand command) {
        return delegate.updateWatchlistStock(userId, watchlistId, command);
    }

    @Override
    @Transactional
    public void deleteWatchlist(Long userId, Long watchlistId) {
        delegate.deleteWatchlist(userId, watchlistId);
    }
}
