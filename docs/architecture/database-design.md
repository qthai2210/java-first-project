# Database Design & Schema Architecture

Tài liệu này mô tả chi tiết thiết kế lưu trữ dữ liệu của nền tảng Stock Prediction, bao gồm **Cold Storage (PostgreSQL)** dùng để lưu trữ dữ liệu bền vững (Batch) và **Speed Cache (Redis)** dùng cho dữ liệu siêu tốc (Real-time).

## 1. PostgreSQL Schema (Cold Storage)
Đây là cơ sở dữ liệu chính của hệ thống (Single Source of Truth). Nó lưu trữ thông tin User, dữ liệu lịch sử EOD (End of Day) và các dự đoán dài hạn từ AI.

### Entity Relationship Diagram (ERD)
```mermaid
erDiagram
    USERS {
        uuid id PK
        string username
        string password_hash
        string email
        datetime created_at
    }
    STOCK_SYMBOLS {
        string symbol PK "e.g., FPT, HPG"
        string company_name
        string exchange "HOSE, HNX, UPCOM"
    }
    K_LINE_DATA {
        uuid id PK
        string symbol FK
        date trade_date
        decimal open_price
        decimal high_price
        decimal low_price
        decimal close_price
        bigint volume
    }
    AI_PREDICTIONS {
        uuid id PK
        string symbol FK
        date target_date
        decimal predicted_price
        decimal confidence_score
        datetime created_at
    }
    USER_ALERTS {
        uuid id PK
        uuid user_id FK
        string symbol FK
        string alert_type "e.g., VOLUME_SPIKE"
        decimal threshold
        boolean is_active
    }

    USERS ||--o{ USER_ALERTS : "tạo_cảnh_báo"
    STOCK_SYMBOLS ||--o{ K_LINE_DATA : "chứa_dữ_liệu_giá"
    STOCK_SYMBOLS ||--o{ AI_PREDICTIONS : "có_dự_đoán"
    STOCK_SYMBOLS ||--o{ USER_ALERTS : "được_theo_dõi"
```

### Chi tiết các Bảng (Tables):
- **`USERS`**: Quản lý tài khoản nhà đầu tư.
- **`STOCK_SYMBOLS`**: Danh mục mã cổ phiếu hỗ trợ. Đóng vai trò là bảng Master Data.
- **`K_LINE_DATA`**: Bảng siêu to khổng lồ chứa dữ liệu lịch sử (EOD). Bảng này cần được đánh Index (Partitioning) theo `trade_date` và `symbol` để tối ưu truy vấn nạp vào AI.
- **`AI_PREDICTIONS`**: Lưu lại kết quả chạy qua đêm của Kronos AI.
- **`USER_ALERTS`**: Lưu cấu hình cảnh báo Real-time của user (Ví dụ: Báo cho tôi khi Volume của HPG tăng gấp 3).

---

## 2. Redis Data Structures (Speed Cache)
Để đáp ứng tốc độ phản hồi tính bằng mili-giây cho sơ đồ C4 Level 2 (Luồng Speed Layer), Redis được sử dụng làm In-Memory DB.

### Thiết kế Key-Value:

1. **Intraday Candles (Nến trong phiên)**
   - **Key:** `market:intraday:candles:{symbol}` (VD: `market:intraday:candles:FPT`)
   - **Data Type:** `Sorted Set (ZSET)`
   - **Mô tả:** Chứa các cây nến 1 phút trong ngày. Score của ZSET chính là UNIX timestamp để dễ dàng query: "Lấy 20 cây nến gần nhất". Dữ liệu bay màu (TTL) vào cuối ngày vì K_LINE_DATA (Postgres) sẽ lo phần lịch sử.

2. **Real-time Spike Lock (Chống Spam Cảnh Báo)**
   - **Key:** `alert:spike:lock:{symbol}`
   - **Data Type:** `String (với TTL = 5 phút)`
   - **Mô tả:** Khi hệ thống phát hiện khối lượng HPG đột biến, nó bắn Alert qua Kafka và Set key này bằng 1. Nếu 2 phút sau khối lượng lại đột biến, Rule Engine check thấy key này vẫn còn (chưa hết TTL), nó sẽ bỏ qua để không spam màn hình User.

3. **Latest Prediction Cache**
   - **Key:** `cache:prediction:latest:{symbol}`
   - **Data Type:** `JSON / String`
   - **Mô tả:** Lưu lại kết quả dự đoán (từ bảng AI_PREDICTIONS) để API của Spring Boot có thể trả về cho Web Dashboard ngay lập tức mà không cần Hits vào PostgreSQL.
