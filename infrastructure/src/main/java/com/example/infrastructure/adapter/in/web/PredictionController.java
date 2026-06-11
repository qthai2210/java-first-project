package com.example.infrastructure.adapter.in.web;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.application.dto.PredictionResponseDto;
import com.example.application.port.in.PredictionServicePort;
import com.example.domain.model.Prediction;
import com.example.infrastructure.mapper.PredictionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/predictions")
@Tag(name = "Prediction Management", description = "Endpoints for viewing AI predictions on stocks")
@SecurityRequirement(name = "bearerAuth")
public class PredictionController {

    private final PredictionServicePort predictionServicePort;
    private final PredictionMapper predictionMapper;

    public PredictionController(PredictionServicePort predictionServicePort, PredictionMapper predictionMapper) {
        this.predictionServicePort = predictionServicePort;
        this.predictionMapper = predictionMapper;
    }

    @GetMapping("/stock/{symbol}/latest")
    @Operation(summary = "Get the latest prediction for a stock")
    public ResponseEntity<PredictionResponseDto> getLatestPrediction(@PathVariable String symbol) {
        log.info("REST request to get latest prediction for stock: {}", symbol);
        Prediction prediction = predictionServicePort.getLatestPrediction(symbol);
        return ResponseEntity.ok(predictionMapper.mapToDto(prediction));
    }

    @GetMapping("/stock/{symbol}")
    @Operation(summary = "Get paginated history of predictions for a stock")
    public ResponseEntity<PageDataDto<PredictionResponseDto>> getPredictions(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "predictedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        log.info("REST request to get predictions for stock: {}, page: {}, size: {}", symbol, page, size);
        PageQueryDto query = new PageQueryDto(page, size, sortBy, sortDirection);
        PageDataDto<Prediction> predictionPageData = predictionServicePort.getPredictions(symbol, query);

        List<PredictionResponseDto> dtoList = predictionPageData.getData().stream()
                .map(predictionMapper::mapToDto)
                .collect(Collectors.toList());

        PageDataDto<PredictionResponseDto> responseData = new PageDataDto<>(
                dtoList,
                predictionPageData.getPage(),
                predictionPageData.getSize(),
                predictionPageData.getTotalElements(),
                predictionPageData.getTotalPages(),
                predictionPageData.isHasNext(),
                predictionPageData.isHasPrevious()
        );

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/stock/{symbol}/generate")
    @Operation(summary = "Request generation of a new prediction for a stock (triggers background ML generation)")
    public ResponseEntity<PredictionResponseDto> generatePrediction(@PathVariable String symbol) {
        log.info("REST request to generate prediction for stock: {}", symbol);
        Prediction created = predictionServicePort.generatePrediction(symbol);
        return ResponseEntity.ok(predictionMapper.mapToDto(created));
    }
}
