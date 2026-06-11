CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    condition_type VARCHAR(50) NOT NULL,
    comparison_operator VARCHAR(10) NOT NULL,
    threshold_value DECIMAL(15, 4) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    triggered_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_alerts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerts_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE
);

CREATE INDEX idx_alerts_user_enabled ON alerts(user_id, is_enabled);
CREATE INDEX idx_alerts_stock_enabled ON alerts(stock_id, is_enabled);
