CREATE TABLE daily_prices (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT NOT NULL,
    price_date DATE NOT NULL,
    open_price DECIMAL(15, 4) NOT NULL,
    high_price DECIMAL(15, 4) NOT NULL,
    low_price DECIMAL(15, 4) NOT NULL,
    close_price DECIMAL(15, 4) NOT NULL,
    volume BIGINT NOT NULL,
    CONSTRAINT fk_daily_prices_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE,
    CONSTRAINT uq_daily_prices_stock_date UNIQUE (stock_id, price_date)
);

CREATE INDEX idx_daily_prices_stock_date ON daily_prices(stock_id, price_date DESC);
