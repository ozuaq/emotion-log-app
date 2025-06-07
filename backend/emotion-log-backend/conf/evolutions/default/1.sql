-- Emotion Log Schema

-- !Ups
-- This section contains the SQL statements to apply to the database.
CREATE TABLE emotion_log (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id       VARCHAR(255) NOT NULL,
    emotion_level VARCHAR(50) NOT NULL,
    memo          TEXT,
    recorded_at   TIMESTAMP NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- !Downs
-- This section contains the SQL statements to revert the database changes.
DROP TABLE IF EXISTS emotion_log;
