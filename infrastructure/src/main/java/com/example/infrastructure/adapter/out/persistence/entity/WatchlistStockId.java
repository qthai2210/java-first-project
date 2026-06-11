package com.example.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistStockId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "watchlist_id")
    private Long watchlistId;

    @Column(name = "stock_id")
    private Long stockId;
}
