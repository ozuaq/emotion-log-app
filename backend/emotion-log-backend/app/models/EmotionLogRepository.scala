package models

import java.time.Instant
import javax.inject.{Inject, Singleton}
import play.api.db.Database // PlayのDB接続
import anorm._ // Anormのコアライブラリ
import anorm.SqlParser._ // Anormのパーサー

// EmotionLogRepositoryで利用するEmotionLogとEmotionLevelをインポート
// (同じパッケージなので通常は不要ですが、明示的に記述)
import models.{EmotionLog, EmotionLevel}

@Singleton
class EmotionLogRepository @Inject() (db: Database)(implicit
    ec: scala.concurrent.ExecutionContext
) {

  // EmotionLogオブジェクトをデータベースの行からマッピングするためのAnormパーサー
  private val emotionLogParser: RowParser[EmotionLog] = {
    get[Option[Long]]("id") ~ // idカラム (Option[Long]として取得)
      get[String]("user_id") ~ // user_idカラム
      get[String](
        "emotion_level"
      ) ~ // emotion_levelカラム (Stringとして取得し、後でEmotionLevelに変換)
      get[Option[String]]("memo") ~ // memoカラム
      get[Instant]("recorded_at") map { // recorded_atカラム (Instantとして取得)
        case id ~ userId ~ emotionLevelStr ~ memo ~ recordedAt =>
          EmotionLog(
            id,
            userId,
            EmotionLevel
              .fromString(emotionLevelStr)
              .getOrElse(EmotionLevel.Neutral), // 文字列からEmotionLevelに変換
            memo,
            recordedAt
          )
      }
  }

  // 新しい感情ログを作成 (IDが自動採番されることを期待)
  def create(
      userId: String,
      emotionLevel: EmotionLevel,
      memo: Option[String],
      recordedAt: Instant
  ): Option[Long] = {
    db.withConnection { implicit connection =>
      SQL"""
        INSERT INTO emotion_log (user_id, emotion_level, memo, recorded_at)
        VALUES (${userId}, ${emotionLevel.value}, ${memo}, ${recordedAt})
      """.executeInsert() // executeInsertはOption[Long] (生成されたID) を返す
    }
  }

  // IDで感情ログを検索
  def findById(id: Long): Option[EmotionLog] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM emotion_log WHERE id = ${id}".as(
        emotionLogParser.singleOpt
      )
    }
  }

  // 特定のユーザーの全ての感情ログを取得 (recorded_atで降順ソート)
  def findAllByUserId(userId: String): List[EmotionLog] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM emotion_log WHERE user_id = ${userId} ORDER BY recorded_at DESC"
        .as(emotionLogParser.*) // .as(parser.*) でList[T]を取得
    }
  }

  // 感情ログを更新 (IDと更新内容を指定)
  def update(
      id: Long,
      emotionLevel: EmotionLevel,
      memo: Option[String],
      recordedAt: Instant
  ): Int = {
    db.withConnection { implicit connection =>
      SQL"""
        UPDATE emotion_log
        SET emotion_level = ${emotionLevel.value}, memo = ${memo}, recorded_at = ${recordedAt}, updated_at = CURRENT_TIMESTAMP
        WHERE id = ${id}
      """.executeUpdate() // executeUpdateは更新された行数を返す (Int)
    }
  }

  // IDで感情ログを削除
  def delete(id: Long): Int = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM emotion_log WHERE id = ${id}".executeUpdate()
    }
  }

  // (オプション) 全ての感情ログを取得 (デバッグ用など)
  def listAll(): List[EmotionLog] = {
    db.withConnection { implicit c =>
      SQL"SELECT * FROM emotion_log".as(emotionLogParser.*)
    }
  }
}
