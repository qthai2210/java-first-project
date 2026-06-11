package com.example.application.service;

import com.example.application.port.in.PredictionServicePort;
import com.example.application.port.out.PredictionPersistencePort;
import com.example.application.port.out.StockPersistencePort;
import com.example.domain.exception.ResourceNotFoundException;
import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.Prediction;
import com.example.domain.model.Stock;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PredictionService implements PredictionServicePort {

    private final PredictionPersistencePort predictionPersistencePort;
    private final StockPersistencePort stockPersistencePort;

    public PredictionService(PredictionPersistencePort predictionPersistencePort,
                             StockPersistencePort stockPersistencePort) {
        this.predictionPersistencePort = predictionPersistencePort;
        this.stockPersistencePort = stockPersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public Prediction getLatestPrediction(String symbol) {
        log.debug("Fetching latest prediction for stock symbol: {}", symbol);
        Stock stock = stockPersistencePort.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock with symbol " + symbol + " not found"));

        return predictionPersistencePort.findLatestByStockId(stock.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No predictions found for stock " + symbol));
    }

    @Override
    @Transactional(readOnly = true)
    public PageDataDto<Prediction> getPredictions(String symbol, PageQueryDto query) {
        log.debug("Fetching all predictions list for stock symbol: {}", symbol);
        Stock stock = stockPersistencePort.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock with symbol " + symbol + " not found"));

        return predictionPersistencePort.findByStockId(stock.getId(), query);
    }

    @Override
    @Transactional
    public Prediction generatePrediction(String symbol) {
        // TODO: Integrate with external Python ML Prediction service in Phase 7
        throw new UnsupportedOperationException("ML Prediction generation is not supported yet for symbol: " + symbol);
    }
}
