package com.example.infrastructure.adapter.out.persistence.repository;

import com.example.infrastructure.adapter.out.persistence.entity.WatchlistStockId;
import com.example.infrastructure.adapter.out.persistence.entity.WatchlistStockJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistStockJpaRepository extends JpaRepository<WatchlistStockJpaEntity, WatchlistStockId> {
}
