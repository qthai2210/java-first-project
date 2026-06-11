CREATE TABLE watchlists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_watchlists_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_watchlists_user_name UNIQUE (user_id, name)
);

CREATE TABLE watchlist_stocks (
    watchlist_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    added_at TIMESTAMP NOT NULL,
    notes TEXT,
    target_price DECIMAL(15, 4),
    stop_loss DECIMAL(15, 4),
    PRIMARY KEY (watchlist_id, stock_id),
    CONSTRAINT fk_ws_watchlist FOREIGN KEY (watchlist_id) REFERENCES watchlists(id) ON DELETE CASCADE,
    CONSTRAINT fk_ws_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE
);

CREATE INDEX idx_watchlists_user ON watchlists(user_id);
