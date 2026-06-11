package com.example.infrastructure.adapter.out.persistence.adapter;

import com.example.application.port.out.StockPersistencePort;
import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.Stock;
import com.example.infrastructure.adapter.out.persistence.entity.StockJpaEntity;
import com.example.infrastructure.adapter.out.persistence.repository.StockJpaRepository;
import com.example.infrastructure.mapper.PaginationMapper;
import com.example.infrastructure.mapper.StockMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class StockPersistenceAdapter implements StockPersistencePort {

    private final StockJpaRepository stockJpaRepository;
    private final StockMapper stockMapper;

    public StockPersistenceAdapter(StockJpaRepository stockJpaRepository, StockMapper stockMapper) {
        this.stockJpaRepository = stockJpaRepository;
        this.stockMapper = stockMapper;
    }

    @Override
    public Stock save(Stock stock) {
        StockJpaEntity entity = stockMapper.mapToJpaEntity(stock);
        StockJpaEntity savedEntity = stockJpaRepository.save(entity);
        return stockMapper.mapToDomain(savedEntity);
    }

    @Override
    public Optional<Stock> findById(Long id) {
        return stockJpaRepository.findById(id).map(stockMapper::mapToDomain);
    }

    @Override
    public Optional<Stock> findBySymbol(String symbol) {
        return stockJpaRepository.findBySymbol(symbol).map(stockMapper::mapToDomain);
    }

    @Override
    public PageDataDto<Stock> findAll(PageQueryDto query) {
        Sort.Direction direction = Sort.Direction.fromString(query.getSortDirection());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), Sort.by(direction, query.getSortBy()));
        Page<StockJpaEntity> page = stockJpaRepository.findAll(pageable);
        return PaginationMapper.map(page, stockMapper::mapToDomain);
    }

    @Override
    public List<Stock> searchByQuery(String query) {
        return stockJpaRepository.searchByQuery(query).stream()
                .map(stockMapper::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return stockJpaRepository.existsById(id);
    }

    @Override
    public boolean existsBySymbol(String symbol) {
        return stockJpaRepository.existsBySymbol(symbol);
    }

    @Override
    public void deleteById(Long id) {
        stockJpaRepository.deleteById(id);
    }
}
