package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.{EmotionLogRepository, EmotionLog, EmotionLevel}
import java.time.LocalDate
import scala.util.Try // Tryをインポート

// JSONのやり取りで使うデータ構造とフォーマットを定義するコンパニオンオブジェクト
object EmotionLogController {
  // EmotionLogをJSONに変換するためのフォーマット
  // EmotionLevel.emotionLevelFormatがスコープにあれば、これは自動で導出される
  implicit val emotionLogFormat: Format[EmotionLog] = Json.format[EmotionLog]

  // APIリクエストのボディ(ペイロード)を表すケースクラス
  case class EmotionLogPayload(
      userId: Long,
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
  def listByUser(userId: Long): Action[AnyContent] = Action {
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

  /** サンプルデータを生成するAPIエンドポイント クエリパラメータで ?year=YYYY&month=M のように年月を指定
    */
  def seed(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      // クエリ文字列からyearとmonthを安全に取得
      val yearOpt = request.queryString
        .get("year")
        .flatMap(_.headOption.flatMap(s => Try(s.toInt).toOption))
      val monthOpt = request.queryString
        .get("month")
        .flatMap(_.headOption.flatMap(s => Try(s.toInt).toOption))

      // for内包表記を使って、yearとmonthの両方が存在する場合のみ処理を実行
      (for {
        year <- yearOpt
        month <- monthOpt
      } yield {
        try {
          val insertedRows = emotionLogRepo.seed(year, month)
          Ok(
            Json.obj(
              "status" -> "OK",
              "message" -> s"${year}年${month}月分のサンプルデータ（${insertedRows}件）を生成しました。"
            )
          )
        } catch {
          case e: java.time.DateTimeException =>
            BadRequest(
              Json.obj(
                "status" -> "KO",
                "message" -> "無効な年月が指定されました。",
                "error" -> e.getMessage
              )
            )
          case e: Exception =>
            InternalServerError(
              Json.obj(
                "status" -> "KO",
                "message" -> "サンプルデータの生成中にエラーが発生しました。",
                "error" -> e.getMessage
              )
            )
        }
      }).getOrElse {
        // yearまたはmonthが指定されていない場合はBadRequestを返す
        BadRequest(
          Json.obj(
            "status" -> "KO",
            "message" -> "クエリパラメータ 'year' と 'month' を指定してください。例: ?year=2025&month=5"
          )
        )
      }
  }
}
