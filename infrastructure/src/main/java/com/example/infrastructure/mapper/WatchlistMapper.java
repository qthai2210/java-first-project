package com.example.infrastructure.mapper;

import com.example.application.dto.WatchlistRequestDto;
import com.example.application.dto.WatchlistResponseDto;
import com.example.application.dto.WatchlistStockResponseDto;
import com.example.domain.model.Watchlist;
import com.example.domain.model.WatchlistStock;
import com.example.infrastructure.adapter.out.persistence.entity.WatchlistJpaEntity;
import com.example.infrastructure.adapter.out.persistence.entity.WatchlistStockJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class, StockMapper.class})
public interface WatchlistMapper {

    Watchlist mapToDomain(WatchlistRequestDto dto);

    @Mapping(source = "user.id", target = "userId")
    WatchlistResponseDto mapToDto(Watchlist domain);

    @Mapping(source = "stock.symbol", target = "symbol")
    @Mapping(source = "stock.name", target = "name")
    @Mapping(source = "stock.currentPrice", target = "currentPrice")
    WatchlistStockResponseDto mapToDto(WatchlistStock domain);

    @Mapping(target = "user", ignore = true)
    WatchlistJpaEntity mapToJpaEntity(Watchlist domain);

    Watchlist mapToDomain(WatchlistJpaEntity entity);

    @Mapping(target = "watchlist", ignore = true)
    WatchlistStockJpaEntity mapToJpaEntity(WatchlistStock domain);

    WatchlistStock mapToDomain(WatchlistStockJpaEntity entity);
}
