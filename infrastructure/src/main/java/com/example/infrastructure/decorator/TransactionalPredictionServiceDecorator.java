package com.example.infrastructure.decorator;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.application.port.in.PredictionServicePort;
import com.example.domain.model.Prediction;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalPredictionServiceDecorator implements PredictionServicePort {

    private final PredictionServicePort delegate;

    public TransactionalPredictionServiceDecorator(PredictionServicePort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(readOnly = true)
    public Prediction getLatestPrediction(String symbol) {
        return delegate.getLatestPrediction(symbol);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDataDto<Prediction> getPredictions(String symbol, PageQueryDto query) {
        return delegate.getPredictions(symbol, query);
    }

    @Override
    @Transactional
    public Prediction generatePrediction(String symbol) {
        return delegate.generatePrediction(symbol);
    }
}
