package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.{EmotionLogRepository, EmotionLog, EmotionLevel}
import java.time.Instant

// JSONのやり取りで使うデータ構造とフォーマットを定義するコンパニオンオブジェクト
object EmotionLogController {
  // EmotionLogをJSONに変換するためのフォーマット
  // EmotionLevel.emotionLevelFormatがスコープにあれば、これは自動で導出される
  implicit val emotionLogFormat: Format[EmotionLog] = Json.format[EmotionLog]

  // APIリクエストのボディ(ペイロード)を表すケースクラス
  case class EmotionLogPayload(
      userId: String,
      emotionLevel: EmotionLevel,
      memo: Option[String],
      recordedAt: Option[Instant] // 新規作成/更新時に日時の指定がなければ現在時刻を使う
  )

  // ペイロードをJSONから読み込むためのフォーマット
  implicit val emotionLogPayloadReads: Reads[EmotionLogPayload] =
    Json.reads[EmotionLogPayload]
}

@Singleton
class EmotionLogController @Inject() (
    val controllerComponents: ControllerComponents,
    emotionLogRepo: EmotionLogRepository // データベース操作用のリポジトリをインジェクト
) extends BaseController {

  import EmotionLogController._ // コンパニオンオブジェクトのimplicitをインポート

  // --- Emotion Log CRUD Endpoints ---

  // 新しい感情ログを作成 (POST /api/logs)
  def create(): Action[JsValue] = Action(parse.json) {
    implicit request: Request[JsValue] =>
      request.body
        .validate[EmotionLogPayload]
        .fold(
          errors =>
            BadRequest(
              Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))
            ),
          payload => {
            val recordedAt = payload.recordedAt.getOrElse(Instant.now())
            emotionLogRepo.create(
              payload.userId,
              payload.emotionLevel,
              payload.memo,
              recordedAt
            ) match {
              case Some(id) =>
                Created(
                  Json.obj(
                    "status" -> "OK",
                    "message" -> "Emotion log created",
                    "id" -> id
                  )
                )
              case None =>
                InternalServerError(
                  Json.obj(
                    "status" -> "KO",
                    "message" -> "Could not create emotion log"
                  )
                )
            }
          }
        )
  }

  // 特定ユーザーの感情ログ一覧を取得 (GET /api/logs/user/:userId)
  def listByUser(userId: String): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      val logs = emotionLogRepo.findAllByUserId(userId)
      Ok(Json.toJson(logs))
  }

  // IDで特定の感情ログを取得 (GET /api/logs/:id)
  def findById(id: Long): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      emotionLogRepo.findById(id) match {
        case Some(log) => Ok(Json.toJson(log))
        case None =>
          NotFound(
            Json.obj("status" -> "KO", "message" -> "Emotion log not found")
          )
      }
  }

  // 感情ログを更新 (PUT /api/logs/:id)
  def update(id: Long): Action[JsValue] = Action(parse.json) {
    implicit request: Request[JsValue] =>
      request.body
        .validate[EmotionLogPayload]
        .fold(
          errors =>
            BadRequest(
              Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))
            ),
          payload => {
            val recordedAt = payload.recordedAt.getOrElse(Instant.now())
            val updatedRows = emotionLogRepo
              .update(id, payload.emotionLevel, payload.memo, recordedAt)
            if (updatedRows > 0) {
              Ok(
                Json.obj(
                  "status" -> "OK",
                  "message" -> "Emotion log updated",
                  "id" -> id
                )
              )
            } else {
              NotFound(
                Json.obj(
                  "status" -> "KO",
                  "message" -> "Emotion log not found or not updated"
                )
              )
            }
          }
        )
  }

  // 感情ログを削除 (DELETE /api/logs/:id)
  def delete(id: Long): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      val deletedRows = emotionLogRepo.delete(id)
      if (deletedRows > 0) {
        Ok(
          Json.obj(
            "status" -> "OK",
            "message" -> "Emotion log deleted",
            "id" -> id
          )
        )
      } else {
        NotFound(
          Json.obj("status" -> "KO", "message" -> "Emotion log not found")
        )
      }
  }

  // (デバッグ用) 全ての感情ログを取得 (GET /api/logs)
  def listAll(): Action[AnyContent] = Action { implicit request =>
    Ok(Json.toJson(emotionLogRepo.listAll()))
  }
}
