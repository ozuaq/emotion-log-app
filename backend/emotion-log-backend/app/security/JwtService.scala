package security

import javax.inject.Inject
import play.api.Configuration
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import java.time.Clock

class JwtService @Inject() (config: Configuration) {

  // application.confから秘密鍵を読み込む
  private val secretKey = config.get[String]("jwt.secret")
  private val algorithm = JwtAlgorithm.HS256

  // ClockをDIすることで、テスト時に時間を操作しやすくする (今回はデフォルトを使用)
  implicit val clock: Clock = Clock.systemUTC

  /** ユーザーIDを元にJWTを生成する
    * @param userId
    *   ユーザーID
    * @return
    *   生成されたJWT文字列
    */
  def generateToken(userId: Long): String = {
    val claim = JwtClaim(
      subject = Some(userId.toString), // トークンの主題としてユーザーIDを設定
      issuedAt = Some(clock.instant().getEpochSecond),
      expiration = Some(
        clock.instant().plusSeconds(3600 * 24).getEpochSecond
      ) // 例: 24時間後に失効
    )
    Jwt.encode(claim, secretKey, algorithm)
  }

  /** JWTが有効か検証する
    * @param token
    *   検証するJWT文字列
    * @return
    *   検証に成功すれば、ユーザーIDを含むJwtClaim
    */
  def validateToken(token: String): Option[JwtClaim] = {
    Jwt.decode(token, secretKey, Seq(algorithm)).toOption
  }
}
