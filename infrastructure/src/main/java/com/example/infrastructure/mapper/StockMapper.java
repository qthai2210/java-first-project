package com.example.infrastructure.mapper;

import com.example.infrastructure.adapter.in.web.dto.StockRequestDto;
import com.example.application.dto.StockResponseDto;
import com.example.domain.model.Stock;
import com.example.infrastructure.adapter.out.persistence.entity.StockJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockMapper {

    Stock mapToDomain(StockRequestDto dto);

    StockResponseDto mapToDto(Stock domain);

    StockJpaEntity mapToJpaEntity(Stock domain);

    Stock mapToDomain(StockJpaEntity entity);
}
