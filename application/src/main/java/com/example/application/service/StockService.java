package com.example.application.service;

import com.example.application.port.in.StockServicePort;
import com.example.application.port.out.StockPersistencePort;
import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResourceNotFoundException;
import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.Stock;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class StockService implements StockServicePort {

    private final StockPersistencePort stockPersistencePort;

    public StockService(StockPersistencePort stockPersistencePort) {
        this.stockPersistencePort = stockPersistencePort;
    }

    @Override
    @Transactional
    public Stock createStock(Stock stock) {
        log.debug("Creating stock with symbol: {}", stock.getSymbol());
        if (stockPersistencePort.existsBySymbol(stock.getSymbol())) {
            log.warn("Stock creation failed: '{}' is already registered", stock.getSymbol());
            throw new DomainException("Stock symbol '" + stock.getSymbol() + "' is already registered");
        }
        
        Stock stockToSave = stock.toBuilder()
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
                
        Stock savedStock = stockPersistencePort.save(stockToSave);
        log.info("Stock registered successfully with ID: {}", savedStock.getId());
        return savedStock;
    }

    @Override
    @Transactional(readOnly = true)
    public Stock getStockById(Long id) {
        log.debug("Fetching stock by ID: {}", id);
        return stockPersistencePort.findById(id)
                .orElseThrow(() -> {
                    log.warn("Stock with ID: {} not found", id);
                    return new ResourceNotFoundException("Stock with ID " + id + " not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Stock getStockBySymbol(String symbol) {
        log.debug("Fetching stock by symbol: {}", symbol);
        return stockPersistencePort.findBySymbol(symbol)
                .orElseThrow(() -> {
                    log.warn("Stock with symbol: {} not found", symbol);
                    return new ResourceNotFoundException("Stock with symbol " + symbol + " not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PageDataDto<Stock> getAllStocks(PageQueryDto query) {
        log.debug("Fetching all stocks list with page {} size {}", query.getPage(), query.getSize());
        return stockPersistencePort.findAll(query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> searchStocks(String query) {
        log.debug("Searching stocks for query: {}", query);
        return stockPersistencePort.searchByQuery(query);
    }

    @Override
    @Transactional
    public Stock updateStock(Long id, Stock stockDetails) {
        log.debug("Updating stock details for ID: {}", id);
        Stock existingStock = getStockById(id);

        if (!existingStock.getSymbol().equalsIgnoreCase(stockDetails.getSymbol()) 
                && stockPersistencePort.existsBySymbol(stockDetails.getSymbol())) {
            log.warn("Failed to update stock ID: {}: Symbol '{}' is already registered", id, stockDetails.getSymbol());
            throw new DomainException("Stock symbol '" + stockDetails.getSymbol() + "' is already registered");
        }

        Stock updatedStock = existingStock.toBuilder()
                .symbol(stockDetails.getSymbol().toUpperCase())
                .name(stockDetails.getName())
                .exchange(stockDetails.getExchange())
                .sector(stockDetails.getSector())
                .marketCap(stockDetails.getMarketCap())
                .currency(stockDetails.getCurrency())
                .build();

        Stock savedStock = stockPersistencePort.save(updatedStock);
        log.info("Stock with ID: {} updated successfully in database", id);
        return savedStock;
    }

    @Override
    @Transactional
    public void deleteStock(Long id) {
        log.debug("Deleting stock with ID: {}", id);
        Stock stock = getStockById(id);
        Stock deactivatedStock = stock.toBuilder().active(false).build();
        stockPersistencePort.save(deactivatedStock);
        log.info("Stock with ID: {} soft deleted successfully from database", id);
    }

    @Override
    @Transactional
    public Stock updatePrice(String symbol, BigDecimal price) {
        log.debug("Updating price for stock {}: {}", symbol, price);
        Stock stock = getStockBySymbol(symbol);
        Stock updated = stock.toBuilder()
                .currentPrice(price)
                .lastPriceUpdate(LocalDateTime.now())
                .build();
        return stockPersistencePort.save(updated);
    }
}
