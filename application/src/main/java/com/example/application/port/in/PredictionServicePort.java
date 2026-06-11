package com.example.application.port.in;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.Prediction;

public interface PredictionServicePort {
    Prediction getLatestPrediction(String symbol);
    PageDataDto<Prediction> getPredictions(String symbol, PageQueryDto query);
    Prediction generatePrediction(String symbol);
}
