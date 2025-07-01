package controllers

import javax.inject._
import models.{User, UserDAO}
import play.api.libs.json._
import play.api.mvc._
import security.{JwtService, SecuredAction, UserRequest}
import org.springframework.security.crypto.password.PasswordEncoder

object AuthController {
  // サインアップリクエストのJSONをパースするためのケースクラス
  case class SignUpRequest(
      email: String,
      password: String,
      name: Option[String]
  )
  implicit val signUpRequestReads: Reads[SignUpRequest] =
    Json.reads[SignUpRequest]

  // ログインリクエストのJSONをパースするためのケースクラス
  case class LoginRequest(email: String, password: String)
  implicit val loginRequestReads: Reads[LoginRequest] = Json.reads[LoginRequest]
}

@Singleton
class AuthController @Inject() (
    val controllerComponents: ControllerComponents,
    userDAO: UserDAO,
    jwtService: JwtService,
    passwordEncoder: PasswordEncoder,
    securedAction: SecuredAction // JWTを検証するカスタムアクション
) extends BaseController {

  import AuthController._
  // UserモデルをJSONに変換するためのWritesを定義
  implicit val userWrites: Writes[User] = Json.writes[User]

  /** ユーザー登録（サインアップ）
    */
  def signUp: Action[JsValue] = Action(parse.json) {
    implicit request: Request[JsValue] =>
      request.body
        .validate[SignUpRequest]
        .fold(
          errors =>
            BadRequest(
              Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))
            ),
          signUpRequest => {
            userDAO.findByEmail(signUpRequest.email) match {
              case Some(_) =>
                Conflict(
                  Json
                    .obj("status" -> "KO", "message" -> "このメールアドレスは既に使用されています。")
                )
              case None =>
                val hashedPassword =
                  passwordEncoder.encode(signUpRequest.password)
                userDAO.create(
                  signUpRequest.email,
                  hashedPassword,
                  signUpRequest.name
                ) match {
                  case Some(id) =>
                    Created(
                      Json.obj(
                        "status" -> "OK",
                        "message" -> "ユーザー登録が完了しました。",
                        "id" -> id
                      )
                    )
                  case None =>
                    InternalServerError(
                      Json.obj("status" -> "KO", "message" -> "ユーザー登録に失敗しました。")
                    )
                }
            }
          }
        )
  }

  /** ログイン処理を行い、JWTを生成して返す
    */
  def login: Action[JsValue] = Action(parse.json) {
    implicit request: Request[JsValue] =>
      request.body
        .validate[LoginRequest]
        .fold(
          errors =>
            BadRequest(
              Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))
            ),
          loginRequest => {
            userDAO.findByEmail(loginRequest.email) match {
              case Some(userWithPassword) =>
                if (
                  passwordEncoder
                    .matches(loginRequest.password, userWithPassword.password)
                ) {
                  val token = jwtService.generateToken(userWithPassword.id)
                  Ok(Json.obj("status" -> "OK", "token" -> token))
                } else {
                  Unauthorized(
                    Json.obj(
                      "status" -> "KO",
                      "message" -> "メールアドレスまたはパスワードが正しくありません。"
                    )
                  )
                }
              case None =>
                Unauthorized(
                  Json.obj(
                    "status" -> "KO",
                    "message" -> "メールアドレスまたはパスワードが正しくありません。"
                  )
                )
            }
          }
        )
  }

  /** 認証済みユーザーのプロフィール情報を返す
    */
  def getProfile: Action[AnyContent] = securedAction {
    implicit request: UserRequest[AnyContent] =>
      val userId = request.userId

      userDAO.findById(userId) match {
        case Some(user) =>
          Ok(Json.toJson(user))
        case None =>
          NotFound(Json.obj("status" -> "KO", "message" -> "User not found"))
      }
  }
}
