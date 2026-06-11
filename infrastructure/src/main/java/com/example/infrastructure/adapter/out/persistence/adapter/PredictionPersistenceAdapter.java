package com.example.infrastructure.adapter.out.persistence.adapter;

import com.example.application.port.out.PredictionPersistencePort;
import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.Prediction;
import com.example.infrastructure.adapter.out.persistence.entity.PredictionJpaEntity;
import com.example.infrastructure.adapter.out.persistence.repository.PredictionJpaRepository;
import com.example.infrastructure.mapper.PaginationMapper;
import com.example.infrastructure.mapper.PredictionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PredictionPersistenceAdapter implements PredictionPersistencePort {

    private final PredictionJpaRepository predictionJpaRepository;
    private final PredictionMapper predictionMapper;

    public PredictionPersistenceAdapter(PredictionJpaRepository predictionJpaRepository, PredictionMapper predictionMapper) {
        this.predictionJpaRepository = predictionJpaRepository;
        this.predictionMapper = predictionMapper;
    }

    @Override
    public Prediction save(Prediction prediction) {
        PredictionJpaEntity entity = predictionMapper.mapToJpaEntity(prediction);
        PredictionJpaEntity saved = predictionJpaRepository.save(entity);
        return predictionMapper.mapToDomain(saved);
    }

    @Override
    public Optional<Prediction> findLatestByStockId(Long stockId) {
        return predictionJpaRepository.findFirstByStockIdOrderByPredictedAtDesc(stockId)
                .map(predictionMapper::mapToDomain);
    }

    @Override
    public PageDataDto<Prediction> findByStockId(Long stockId, PageQueryDto query) {
        Sort.Direction direction = Sort.Direction.fromString(query.getSortDirection());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), Sort.by(direction, query.getSortBy()));
        Page<PredictionJpaEntity> page = predictionJpaRepository.findByStockId(stockId, pageable);
        return PaginationMapper.map(page, predictionMapper::mapToDomain);
    }
}
