package com.example.infrastructure.adapter.out.persistence.repository;

import com.example.infrastructure.adapter.out.persistence.entity.WatchlistJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistJpaRepository extends JpaRepository<WatchlistJpaEntity, Long> {
    List<WatchlistJpaEntity> findByUserId(Long userId);
    Optional<WatchlistJpaEntity> findByUserIdAndName(Long userId, String name);
    boolean existsByUserIdAndName(Long userId, String name);
}
