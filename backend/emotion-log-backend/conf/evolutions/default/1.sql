-- Emotion Log Schema

-- !Ups
-- This section contains the SQL statements to apply to the database.
CREATE TABLE emotion_log (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER NOT NULL,
    log_date      DATE NOT NULL, -- 記録対象の日付
    emotion_level VARCHAR(50) NOT NULL,
    memo          TEXT,
    recorded_at   TIMESTAMP NOT NULL, -- 実際に記録操作が行われた日時
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

-- ユーザーごと、日付ごとに記録は一つだけという制約
CREATE UNIQUE INDEX uq_user_log_date ON emotion_log(user_id, log_date);

-- !Downs
-- This section contains the SQL statements to revert the database changes.
DROP INDEX IF EXISTS uq_user_log_date;
DROP TABLE IF EXISTS emotion_log;
