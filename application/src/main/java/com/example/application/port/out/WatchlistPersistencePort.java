package com.example.application.port.out;

import com.example.domain.model.Watchlist;

import java.util.List;
import java.util.Optional;

public interface WatchlistPersistencePort {
    Watchlist save(Watchlist watchlist);
    Optional<Watchlist> findById(Long id);
    List<Watchlist> findByUserId(Long userId);
    Optional<Watchlist> findByUserIdAndName(Long userId, String name);
    boolean existsById(Long id);
    boolean existsByUserIdAndName(Long userId, String name);
    void deleteById(Long id);
}
