package com.example.infrastructure.adapter.out.persistence.repository;

import com.example.infrastructure.adapter.out.persistence.entity.AlertJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertJpaRepository extends JpaRepository<AlertJpaEntity, Long> {
    List<AlertJpaEntity> findByUserId(Long userId);
    List<AlertJpaEntity> findByStockIdAndEnabledTrue(Long stockId);
}
