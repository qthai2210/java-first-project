package com.example.infrastructure.adapter.out.persistence.repository;

import com.example.infrastructure.adapter.out.persistence.entity.StockJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockJpaRepository extends JpaRepository<StockJpaEntity, Long> {
    Optional<StockJpaEntity> findBySymbol(String symbol);
    boolean existsBySymbol(String symbol);

    @Query("SELECT s FROM StockJpaEntity s WHERE " +
           "LOWER(s.symbol) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<StockJpaEntity> searchByQuery(@Param("query") String query);
}
