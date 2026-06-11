package com.example.infrastructure.adapter.in.web;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.application.dto.StockRequestDto;
import com.example.application.dto.StockResponseDto;
import com.example.application.port.in.StockServicePort;
import com.example.domain.model.Stock;
import com.example.infrastructure.mapper.StockMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/stocks")
@Tag(name = "Stock Management", description = "Endpoints for managing stocks")
@SecurityRequirement(name = "bearerAuth")
public class StockController {

    private final StockServicePort stockServicePort;
    private final StockMapper stockMapper;

    public StockController(StockServicePort stockServicePort, StockMapper stockMapper) {
        this.stockServicePort = stockServicePort;
        this.stockMapper = stockMapper;
    }

    @GetMapping
    @Operation(summary = "Get all stocks")
    public ResponseEntity<PageDataDto<StockResponseDto>> getAllStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "symbol") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        log.info("REST request to retrieve all stocks list, page: {}, size: {}", page, size);
        PageQueryDto query = new PageQueryDto(page, size, sortBy, sortDirection);
        PageDataDto<Stock> stockPageData = stockServicePort.getAllStocks(query);

        List<StockResponseDto> dtoList = stockPageData.getData().stream()
                .map(stockMapper::mapToDto)
                .collect(Collectors.toList());

        PageDataDto<StockResponseDto> responseData = new PageDataDto<>(
                dtoList,
                stockPageData.getPage(),
                stockPageData.getSize(),
                stockPageData.getTotalElements(),
                stockPageData.getTotalPages(),
                stockPageData.isHasNext(),
                stockPageData.isHasPrevious()
        );

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/search")
    @Operation(summary = "Search stocks by symbol or name")
    public ResponseEntity<List<StockResponseDto>> searchStocks(@RequestParam String q) {
        log.info("REST request to search stocks with query: {}", q);
        List<Stock> stocks = stockServicePort.searchStocks(q);
        List<StockResponseDto> response = stocks.stream()
                .map(stockMapper::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a stock by ID")
    public ResponseEntity<StockResponseDto> getStockById(@PathVariable Long id) {
        log.info("REST request to get stock by ID: {}", id);
        Stock stock = stockServicePort.getStockById(id);
        return ResponseEntity.ok(stockMapper.mapToDto(stock));
    }

    @GetMapping("/symbol/{symbol}")
    @Operation(summary = "Get a stock by symbol")
    public ResponseEntity<StockResponseDto> getStockBySymbol(@PathVariable String symbol) {
        log.info("REST request to get stock by symbol: {}", symbol);
        Stock stock = stockServicePort.getStockBySymbol(symbol);
        return ResponseEntity.ok(stockMapper.mapToDto(stock));
    }

    @PostMapping
    @Operation(summary = "Create a new stock (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockResponseDto> createStock(@Valid @RequestBody StockRequestDto request) {
        log.info("REST request to create new stock with symbol: {}", request.getSymbol());
        Stock stockDomain = stockMapper.mapToDomain(request);
        Stock createdStock = stockServicePort.createStock(stockDomain);
        return new ResponseEntity<>(stockMapper.mapToDto(createdStock), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a stock's details (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockResponseDto> updateStock(@PathVariable Long id, @Valid @RequestBody StockRequestDto request) {
        log.info("REST request to update stock with ID: {}", id);
        Stock stockDetails = stockMapper.mapToDomain(request);
        Stock updatedStock = stockServicePort.updateStock(id, stockDetails);
        return ResponseEntity.ok(stockMapper.mapToDto(updatedStock));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a stock by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        log.info("REST request to soft delete stock with ID: {}", id);
        stockServicePort.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}
