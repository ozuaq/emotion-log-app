package models

import javax.inject.{Inject, Singleton}
import play.api.db.Database
import anorm._
import anorm.SqlParser._

// パスワードを含まない、クライアントに返しても安全なUserモデル
case class User(id: Long, email: String, name: Option[String])

@Singleton
class UserDAO @Inject() (db: Database) {

  // DBから取得したユーザー情報とハッシュ化パスワードを格納するケースクラス
  case class UserWithPassword(
      id: Long,
      email: String,
      password: String,
      name: Option[String]
  )

  // DBの行からUserWithPasswordオブジェクトにマッピングするためのパーサー
  private val userWithPasswordParser: RowParser[UserWithPassword] = {
    get[Long]("id") ~
      get[String]("email") ~
      get[String]("password") ~
      get[Option[String]]("name") map { case id ~ email ~ password ~ name =>
        UserWithPassword(id, email, password, name)
      }
  }

  // Userモデル用のパーサー
  private val userParser: RowParser[User] = {
    get[Long]("id") ~
      get[String]("email") ~
      get[Option[String]]("name") map { case id ~ email ~ name =>
        User(id, email, name)
      }
  }

  /** IDでユーザーを検索する (パスワードは含まない)
    */
  def findById(id: Long): Option[User] = db.withConnection { implicit c =>
    SQL"SELECT id, email, name FROM users WHERE id = ${id}".as(
      userParser.singleOpt
    )
  }

  /** Eメールを元にユーザーを検索する (ログイン認証時に使用)
    * @param email
    *   検索するEメール
    * @return
    *   見つかったユーザー情報(パスワード含む)、またはNone
    */
  def findByEmail(email: String): Option[UserWithPassword] = db.withConnection {
    implicit c =>
      SQL"SELECT * FROM users WHERE email = ${email}".as(
        userWithPasswordParser.singleOpt
      )
  }

  /** 新しいユーザーをデータベースに保存する (サインアップ時に使用)
    * @param email
    *   ユーザーのEメール
    * @param password
    *   ハッシュ化済みのパスワード
    * @param name
    *   ユーザー名 (任意)
    * @return
    *   生成されたユーザーのID、またはNone
    */
  def create(
      email: String,
      password: String,
      name: Option[String]
  ): Option[Long] = {
    db.withConnection { implicit c =>
      SQL"""
        INSERT INTO users (email, password, name)
        VALUES (${email}, ${password}, ${name})
      """.executeInsert() // 生成されたIDを返す
    }
  }
}
