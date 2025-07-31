CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email varchar unique not null,
    password varchar,
    score INT DEFAULT 0,
    turns INT DEFAULT 0,
    role_id BIGINT
)

