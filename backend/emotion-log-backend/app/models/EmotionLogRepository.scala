package models

import java.time.{Instant, LocalDate}
import javax.inject.{Inject, Singleton}
import play.api.db.Database // PlayのDB接続
import anorm._ // Anormのコアライブラリ
import anorm.SqlParser._ // Anormのパーサー
import java.time.LocalDate // LocalDateをインポート
import scala.util.Random // Randomをインポート

// EmotionLogRepositoryで利用するEmotionLogとEmotionLevelをインポート
// (同じパッケージなので通常は不要ですが、明示的に記述)
import models.{EmotionLog, EmotionLevel}

@Singleton
class EmotionLogRepository @Inject() (db: Database)(implicit
    ec: scala.concurrent.ExecutionContext
) {

  // DBの行からEmotionLogオブジェクトにマッピングするためのパーサー
  private val emotionLogParser: RowParser[EmotionLog] = {
    get[Option[Long]]("id") ~
      get[Long]("user_id") ~
      get[LocalDate]("log_date") ~ // ★変更
      get[String]("emotion_level") ~
      get[Option[String]]("memo") ~
      get[Instant]("recorded_at") map {
        case id ~ userId ~ logDate ~ emotionLevelStr ~ memo ~ recordedAt =>
          EmotionLog(
            id,
            userId,
            logDate,
            EmotionLevel
              .fromString(emotionLevelStr)
              .getOrElse(EmotionLevel.Neutral),
            memo,
            recordedAt
          )
      }
  }

  // 感情ログを新規作成または更新
  def upsert(
      userId: Long,
      logDate: LocalDate,
      emotionLevel: EmotionLevel,
      memo: Option[String]
  ): Option[Long] = {
    db.withConnection { implicit connection =>
      val existingId: Option[Long] =
        SQL"SELECT id FROM emotion_log WHERE user_id = ${userId} AND log_date = ${logDate}"
          .as(scalar[Long].singleOpt)

      existingId match {
        // 存在する場合: UPDATE
        case Some(id) =>
          SQL"""
            UPDATE emotion_log
            SET emotion_level = ${emotionLevel.value}, memo = ${memo}, recorded_at = ${Instant
              .now()}, updated_at = CURRENT_TIMESTAMP
            WHERE id = ${id}
          """.executeUpdate()
          Some(id) // 更新したログのIDを返す

        // 存在しない場合: INSERT
        case None =>
          SQL"""
            INSERT INTO emotion_log (user_id, log_date, emotion_level, memo, recorded_at)
            VALUES (${userId}, ${logDate}, ${emotionLevel.value}, ${memo}, ${Instant
              .now()})
          """.executeInsert() // 新規作成されたIDを返す
      }
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
  def findAllByUserId(userId: Long): List[EmotionLog] = {
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

  // 以下、デバッグ用のメソッド
  // (オプション) 全ての感情ログを取得 (デバッグ用など)
  def listAll(): List[EmotionLog] = {
    db.withConnection { implicit c =>
      SQL"SELECT * FROM emotion_log".as(emotionLogParser.*)
    }
  }

  /** 指定された年月のサンプルデータをデータベースに上書き(Upsert)する
    * @param year
    *   対象の年
    * @param month
    *   対象の月 (1-12)
    * @return
    *   処理された日数
    */
  def seed(year: Int, month: Int): Int = {
    val userId = 1L
    val random = new Random

    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val daysInMonth = firstDayOfMonth.lengthOfMonth()

    val emotionLevels = List(
      EmotionLevel.VeryGood,
      EmotionLevel.Good,
      EmotionLevel.Neutral,
      EmotionLevel.Bad,
      EmotionLevel.VeryBad
    )
    val memos = List(
      Some("仕事が順調だった"),
      Some("良い天気で散歩した"),
      None,
      Some("少し疲れたかも"),
      Some("面白い本を読んだ"),
      None
    )

    // 既存のDELETE文は使わず、一日ずつupsertを呼び出す
    (0 until daysInMonth).foreach { i =>
      val date = firstDayOfMonth.plusDays(i)
      val level = emotionLevels(random.nextInt(emotionLevels.length))
      val memo = memos(random.nextInt(memos.length))

      // 既存のupsertメソッドを再利用
      upsert(userId, date, level, memo)
    }

    daysInMonth // 処理した日数を返す
  }
}
