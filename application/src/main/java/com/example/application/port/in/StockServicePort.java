package com.example.application.port.in;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.Stock;

import java.math.BigDecimal;
import java.util.List;

public interface StockServicePort {
    Stock createStock(Stock stock);
    Stock getStockById(Long id);
    Stock getStockBySymbol(String symbol);
    PageDataDto<Stock> getAllStocks(PageQueryDto query);
    List<Stock> searchStocks(String query);
    Stock updateStock(Long id, Stock stockDetails);
    void deleteStock(Long id);
    Stock updatePrice(String symbol, BigDecimal price);
}
