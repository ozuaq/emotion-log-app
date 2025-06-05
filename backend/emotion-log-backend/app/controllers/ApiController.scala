package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._

@Singleton
class ApiController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  // シンプルなJSONメッセージを返すアクション
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val jsonResponse = Json.obj(
      "message" -> "Hello from Play Framework API!",
      "status" -> "OK",
      "timestamp" -> java.time.Instant.now().toString
    )
    Ok(jsonResponse)
  }

  // 別のテスト用エンドポイント (例)
  def healthCheck(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(Json.obj("status" -> "Healthy"))
  }
}
