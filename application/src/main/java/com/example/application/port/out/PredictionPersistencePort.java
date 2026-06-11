package com.example.application.port.out;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.Prediction;

import java.util.Optional;

public interface PredictionPersistencePort {
    Prediction save(Prediction prediction);
    Optional<Prediction> findLatestByStockId(Long stockId);
    PageDataDto<Prediction> findByStockId(Long stockId, PageQueryDto query);
}
