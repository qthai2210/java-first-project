CREATE TABLE predictions (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT NOT NULL,
    predicted_price DECIMAL(15, 4) NOT NULL,
    confidence DECIMAL(5, 4) NOT NULL,
    horizon_days INT NOT NULL,
    model_version VARCHAR(50),
    features_json TEXT,
    predicted_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_predictions_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE
);

CREATE INDEX idx_predictions_stock_date ON predictions(stock_id, predicted_at DESC);
