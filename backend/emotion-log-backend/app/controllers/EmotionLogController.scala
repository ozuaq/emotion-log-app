package controllers

import javax.inject._
import models.{EmotionLog, EmotionLogRepository, EmotionLevel, User}
import play.api.libs.json._
import play.api.mvc._
import security.{SecuredAction, UserRequest}
import java.time.LocalDate

object EmotionLogController {
  // ★ 追加: LocalDateをJSONと相互変換するためのフォーマットを定義
  implicit val localDateFormat: Format[LocalDate] = Format(
    Reads.localDateReads("yyyy-MM-dd"),
    Writes.DefaultLocalDateWrites
  )

  // EmotionLogをJSONに変換するためのフォーマット
  implicit val emotionLogFormat: Format[EmotionLog] = Json.format[EmotionLog]

  // APIリクエストのボディ(ペイロード)を表すケースクラス
  // userIdはJWTから取得するため、ここには含めない
  case class EmotionLogPayload(
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
    emotionLogRepo: EmotionLogRepository,
    securedAction: SecuredAction // JWTを検証するカスタムアクションをインジェクト
) extends BaseController {

  import EmotionLogController._

  /** 感情ログを保存（新規作成または更新）する 認証済みのユーザーの記録として保存される
    */
  def upsert(): Action[JsValue] = securedAction(parse.json) {
    implicit request: UserRequest[JsValue] =>
      val userId = request.userId // 認証済みユーザーのIDをリクエストから取得

      request.body
        .validate[EmotionLogPayload]
        .fold(
          errors =>
            BadRequest(
              Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))
            ),
          payload => {
            emotionLogRepo.upsert(
              userId,
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

  /** 認証済みユーザーの感情ログ一覧を取得する
    */
  def list(): Action[AnyContent] = securedAction {
    implicit request: UserRequest[AnyContent] =>
      val userId = request.userId // 認証済みユーザーのIDをリクエストから取得
      val logs = emotionLogRepo.findAllByUserId(userId)
      Ok(Json.toJson(logs))
  }
}
