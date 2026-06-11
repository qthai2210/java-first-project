package com.example.application.port.out;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.Stock;

import java.util.List;
import java.util.Optional;

public interface StockPersistencePort {
    Stock save(Stock stock);
    Optional<Stock> findById(Long id);
    Optional<Stock> findBySymbol(String symbol);
    PageDataDto<Stock> findAll(PageQueryDto query);
    List<Stock> searchByQuery(String query);
    boolean existsById(Long id);
    boolean existsBySymbol(String symbol);
    void deleteById(Long id);
}
