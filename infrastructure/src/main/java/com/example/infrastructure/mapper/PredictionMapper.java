package com.example.infrastructure.mapper;

import com.example.application.dto.PredictionResponseDto;
import com.example.domain.model.Prediction;
import com.example.infrastructure.adapter.out.persistence.entity.PredictionJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {StockMapper.class})
public interface PredictionMapper {

    @Mapping(source = "stock.symbol", target = "stockSymbol")
    @Mapping(source = "stock.name", target = "stockName")
    PredictionResponseDto mapToDto(Prediction domain);

    PredictionJpaEntity mapToJpaEntity(Prediction domain);

    Prediction mapToDomain(PredictionJpaEntity entity);
}
