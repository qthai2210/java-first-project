package com.example.infrastructure.adapter.out.persistence.repository;

import com.example.infrastructure.adapter.out.persistence.entity.PredictionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PredictionJpaRepository extends JpaRepository<PredictionJpaEntity, Long> {
    Optional<PredictionJpaEntity> findFirstByStockIdOrderByPredictedAtDesc(Long stockId);
    Page<PredictionJpaEntity> findByStockId(Long stockId, Pageable pageable);
}
