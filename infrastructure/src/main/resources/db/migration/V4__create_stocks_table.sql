CREATE TABLE stocks (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    name VARCHAR(255) NOT NULL,
    exchange VARCHAR(50),
    sector VARCHAR(100),
    market_cap DECIMAL(20, 2),
    currency VARCHAR(10) DEFAULT 'USD',
    current_price DECIMAL(15, 4),
    last_price_update TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_stocks_symbol UNIQUE (symbol)
);

CREATE INDEX idx_stocks_symbol ON stocks(symbol);
CREATE INDEX idx_stocks_sector ON stocks(sector);
CREATE INDEX idx_stocks_is_active ON stocks(is_active);
