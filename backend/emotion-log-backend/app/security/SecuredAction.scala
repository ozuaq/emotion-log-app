package security

import javax.inject.Inject
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

// 認証済みユーザーの情報をリクエストに含めるためのラッパー
case class UserRequest[A](userId: Long, request: Request[A])
    extends WrappedRequest[A](request)

/** JWTを検証し、認証済みユーザーからのリクエストのみを許可するカスタムActionBuilder
  */
class SecuredAction @Inject() (
    bodyParser: BodyParsers.Default,
    jwtService: JwtService
)(implicit ec: ExecutionContext)
    extends ActionBuilder[UserRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec

  // このアクションが呼び出されたときのメインロジック
  override def invokeBlock[A](
      request: Request[A],
      block: UserRequest[A] => Future[Result]
  ): Future[Result] = {
    // 1. リクエストヘッダーから "Authorization" を取得
    request.headers.get("Authorization") match {
      // 2. ヘッダーが "Bearer <token>" の形式であるかチェック
      case Some(authHeader) if authHeader.startsWith("Bearer ") =>
        val token = authHeader.substring(7) // "Bearer "の部分を削除してトークンを抽出
        // 3. JWTサービスでトークンを検証
        jwtService.validateToken(token) match {
          // 4. 検証成功：トークンからユーザーIDを取り出す
          case Some(claim) =>
            val userId = claim.subject.get.toLong
            // 5. ユーザーIDをリクエストに含めて、本来のコントローラーのアクション(block)を実行
            block(UserRequest(userId, request))
          case None =>
            // トークンが無効または期限切れ
            Future.successful(Results.Unauthorized(s"無効なトークンです"))
        }
      case _ =>
        // Authorizationヘッダーがない、または形式が違う
        Future.successful(Results.Unauthorized("認証ヘッダーが見つかりません"))
    }
  }
}
