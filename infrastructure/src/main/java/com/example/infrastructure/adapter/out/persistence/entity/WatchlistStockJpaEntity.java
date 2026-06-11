package com.example.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "watchlist_stocks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistStockJpaEntity {

    @EmbeddedId
    private WatchlistStockId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("watchlistId")
    @JoinColumn(name = "watchlist_id")
    private WatchlistJpaEntity watchlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("stockId")
    @JoinColumn(name = "stock_id")
    private StockJpaEntity stock;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "target_price", precision = 15, scale = 4)
    private BigDecimal targetPrice;

    @Column(name = "stop_loss", precision = 15, scale = 4)
    private BigDecimal stopLoss;
}
