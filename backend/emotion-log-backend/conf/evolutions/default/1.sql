-- Emotion Log Schema

-- !Ups
-- 'Ups'セクション: データベースに変更を加えるSQLを記述します
CREATE TABLE emotion_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- SQLiteでの自動採番
    user_id VARCHAR(255) NOT NULL,
    emotion_level VARCHAR(50) NOT NULL, -- "very_good", "good", "neutral", "bad", "very_bad"
    memo TEXT,
    recorded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- レコード作成時に自動で現在時刻を設定
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- レコード作成時に自動で現在時刻を設定
);

-- (オプション) updated_atを自動更新するためのトリガー (SQLiteの場合)
-- CREATE TRIGGER update_emotion_log_updated_at
-- AFTER UPDATE ON emotion_log
-- FOR EACH ROW
-- BEGIN
--     UPDATE emotion_log SET updated_at = CURRENT_TIMESTAMP WHERE id = OLD.id;
-- END;


-- !Downs
-- 'Downs'セクション: 'Ups'セクションで行った変更を元に戻すSQLを記述します
DROP TABLE IF EXISTS emotion_log;
