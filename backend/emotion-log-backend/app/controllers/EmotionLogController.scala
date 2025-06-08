package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.{EmotionLogRepository, EmotionLog, EmotionLevel}
import java.time.LocalDate

// JSONのやり取りで使うデータ構造とフォーマットを定義するコンパニオンオブジェクト
object EmotionLogController {
  // EmotionLogをJSONに変換するためのフォーマット
  // EmotionLevel.emotionLevelFormatがスコープにあれば、これは自動で導出される
  implicit val emotionLogFormat: Format[EmotionLog] = Json.format[EmotionLog]

  // APIリクエストのボディ(ペイロード)を表すケースクラス
  case class EmotionLogPayload(
      userId: String,
      logDate: LocalDate,
      emotionLevel: EmotionLevel,
      memo: Option[String]
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

  // 感情ログを新規作成または更新 (POST /api/logs/)
  def upsert(): Action[JsValue] = Action(parse.json) {
    implicit request: Request[JsValue] =>
      request.body
        .validate[EmotionLogPayload]
        .fold(
          errors =>
            BadRequest(
              Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))
            ),
          payload => {
            emotionLogRepo.upsert(
              payload.userId,
              payload.logDate,
              payload.emotionLevel,
              payload.memo
            ) match {
              case Some(id) =>
                Ok(
                  Json.obj(
                    "status" -> "OK",
                    "message" -> "Emotion log saved",
                    "id" -> id
                  )
                )
              case None =>
                InternalServerError(
                  Json.obj(
                    "status" -> "KO",
                    "message" -> "Could not save emotion log"
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

  // 以下、デバッグ用のエンドポイント
  // (デバッグ用) 全ての感情ログを取得 (GET /api/logs)
  def listAll(): Action[AnyContent] = Action { implicit request =>
    Ok(Json.toJson(emotionLogRepo.listAll()))
  }

  /** サンプルデータを生成するAPIエンドポイント
    */
  def seed(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      try {
        val insertedRows = emotionLogRepo.seed()
        Ok(
          Json.obj(
            "status" -> "OK",
            "message" -> s"${insertedRows}件のサンプルデータを生成しました。"
          )
        )
      } catch {
        case e: Exception =>
          InternalServerError(
            Json.obj(
              "status" -> "KO",
              "message" -> "サンプルデータの生成中にエラーが発生しました。",
              "error" -> e.getMessage
            )
          )
      }
  }
}
