package com.example.infrastructure.adapter.out.persistence.adapter;

import com.example.application.port.out.WatchlistPersistencePort;
import com.example.domain.model.Watchlist;
import com.example.infrastructure.adapter.out.persistence.entity.WatchlistJpaEntity;
import com.example.infrastructure.adapter.out.persistence.entity.WatchlistStockJpaEntity;
import com.example.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.example.infrastructure.adapter.out.persistence.repository.WatchlistJpaRepository;
import com.example.infrastructure.mapper.WatchlistMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WatchlistPersistenceAdapter implements WatchlistPersistencePort {

    private final WatchlistJpaRepository watchlistJpaRepository;
    private final WatchlistMapper watchlistMapper;

    public WatchlistPersistenceAdapter(WatchlistJpaRepository watchlistJpaRepository, WatchlistMapper watchlistMapper) {
        this.watchlistJpaRepository = watchlistJpaRepository;
        this.watchlistMapper = watchlistMapper;
    }

    @Override
    public Watchlist save(Watchlist watchlist) {
        WatchlistJpaEntity entity = watchlistMapper.mapToJpaEntity(watchlist);
        
        // Handle bidirectional parent-child mapping for Cascade save
        if (entity.getStocks() != null) {
            for (WatchlistStockJpaEntity stockEntity : entity.getStocks()) {
                stockEntity.setWatchlist(entity);
                // The composite key must be set manually for embeddable ID mapping
                if (stockEntity.getId() == null && entity.getId() != null && stockEntity.getStock() != null) {
                    stockEntity.setId(new com.example.infrastructure.adapter.out.persistence.entity.WatchlistStockId(
                        entity.getId(), stockEntity.getStock().getId()
                    ));
                }
            }
        }

        // Re-inject UserJpaEntity
        if (watchlist.getUser() != null) {
            entity.setUser(UserJpaEntity.builder().id(watchlist.getUser().getId()).build());
        }

        WatchlistJpaEntity savedEntity = watchlistJpaRepository.save(entity);
        return watchlistMapper.mapToDomain(savedEntity);
    }

    @Override
    public Optional<Watchlist> findById(Long id) {
        return watchlistJpaRepository.findById(id).map(watchlistMapper::mapToDomain);
    }

    @Override
    public List<Watchlist> findByUserId(Long userId) {
        return watchlistJpaRepository.findByUserId(userId).stream()
                .map(watchlistMapper::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Watchlist> findByUserIdAndName(Long userId, String name) {
        return watchlistJpaRepository.findByUserIdAndName(userId, name)
                .map(watchlistMapper::mapToDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return watchlistJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByUserIdAndName(Long userId, String name) {
        return watchlistJpaRepository.existsByUserIdAndName(userId, name);
    }

    @Override
    public void deleteById(Long id) {
        watchlistJpaRepository.deleteById(id);
    }
}
