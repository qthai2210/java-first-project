package com.example.infrastructure.mapper;

import com.example.application.dto.AlertResponseDto;
import com.example.domain.model.Alert;
import com.example.infrastructure.adapter.out.persistence.entity.AlertJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {StockMapper.class, UserMapper.class})
public interface AlertMapper {

    @Mapping(source = "stock.symbol", target = "stockSymbol")
    @Mapping(source = "stock.name", target = "stockName")
    AlertResponseDto mapToDto(Alert domain);

    @Mapping(target = "user", ignore = true)
    AlertJpaEntity mapToJpaEntity(Alert domain);

    Alert mapToDomain(AlertJpaEntity entity);
}
