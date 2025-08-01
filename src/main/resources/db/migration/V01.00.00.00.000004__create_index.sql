CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_score_role ON users(role_id, score);