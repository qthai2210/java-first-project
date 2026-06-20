package com.example.infrastructure.decorator;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.application.port.in.StockServicePort;
import com.example.domain.model.Stock;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public class TransactionalStockServiceDecorator implements StockServicePort {

    private final StockServicePort delegate;

    public TransactionalStockServiceDecorator(StockServicePort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public Stock createStock(Stock stock) {
        return delegate.createStock(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public Stock getStockById(Long id) {
        return delegate.getStockById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Stock getStockBySymbol(String symbol) {
        return delegate.getStockBySymbol(symbol);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDataDto<Stock> getAllStocks(PageQueryDto query) {
        return delegate.getAllStocks(query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> searchStocks(String query) {
        return delegate.searchStocks(query);
    }

    @Override
    @Transactional
    public Stock updateStock(Long id, Stock stockDetails) {
        return delegate.updateStock(id, stockDetails);
    }

    @Override
    @Transactional
    public void deleteStock(Long id) {
        delegate.deleteStock(id);
    }

    @Override
    @Transactional
    public Stock updatePrice(String symbol, BigDecimal price) {
        return delegate.updatePrice(symbol, price);
    }
}
