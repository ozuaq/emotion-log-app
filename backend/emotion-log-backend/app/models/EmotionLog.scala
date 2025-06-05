package models

import java.time.Instant // 日付と時刻の扱いにInstantを使用

// 感情の段階を表すためのEnum (またはSealed Trait)
sealed trait EmotionLevel {
  def value: String
}
object EmotionLevel {
  case object VeryGood extends EmotionLevel { val value = "very_good" }
  case object Good extends EmotionLevel { val value = "good" }
  case object Neutral extends EmotionLevel { val value = "neutral" }
  case object Bad extends EmotionLevel { val value = "bad" }
  case object VeryBad extends EmotionLevel { val value = "very_bad" }

  def fromString(s: String): Option[EmotionLevel] = s.toLowerCase match {
    case "very_good" => Some(VeryGood)
    case "good"      => Some(Good)
    case "neutral"   => Some(Neutral)
    case "bad"       => Some(Bad)
    case "very_bad"  => Some(VeryBad)
    case _           => None
  }

  // JSONシリアライズ/デシリアライズ用 (Play JSON)
  import play.api.libs.json._
  implicit val emotionLevelFormat: Format[EmotionLevel] =
    new Format[EmotionLevel] {
      def reads(json: JsValue): JsResult[EmotionLevel] =
        json.asOpt[String].flatMap(fromString) match {
          case Some(level) => JsSuccess(level)
          case None        => JsError("Unknown emotion level")
        }
      def writes(level: EmotionLevel): JsValue = JsString(level.value)
    }
}

// 感情ログのデータを表すケースクラス
case class EmotionLog(
    id: Option[Long], // データベースで自動採番されるID (新規作成時はNone)
    userId: String, // ユーザーID (将来的には認証と連携)
    emotionLevel: EmotionLevel, // 感情の段階 (非常に良い、良い、普通、悪い、非常に悪い)
    memo: Option[String], // 簡単なメモ (任意)
    recordedAt: Instant // 記録日時 (日付のみを扱う場合はLocalDateなども検討)
    // createdAt: Instant,     // 作成日時 (DB側で自動設定も可能)
    // updatedAt: Instant      // 更新日時 (DB側で自動設定も可能)
)

// EmotionLogをJSONに変換するためのPlay JSONのimplicit writer/reader
// (これはコントローラーでJSONを扱う際に必要になりますが、
//  EmotionLevelにFormatを定義したので、ケースクラスには自動的に導出されることもあります)
// object EmotionLog {
//   import play.api.libs.json._
//   implicit val emotionLogFormat: Format[EmotionLog] = Json.format[EmotionLog]
// }
