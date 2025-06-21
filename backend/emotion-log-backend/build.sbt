name := """emotion-log-backend"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

// pac4jのバージョンを指定
val pac4jVersion = "6.0.0"

val jacksonVersion = "2.16.2"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
  jdbc,
  evolutions,
  // Anorm - PlayのシンプルなSQLデータアクセスライブラリ
  "org.playframework.anorm" %% "anorm" % "2.7.0",
  // SQLite JDBCドライバ
  "org.xerial" % "sqlite-jdbc" % "3.45.3.0",
  // JSONシリアライズ/デシリアライズ用のJackson
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  // Play 3.0用のplay-pac4j
  "org.pac4j" %% "play-pac4j" % "12.0.0-PLAY3.0",
  // フォーム認証(ユーザー名/パスワード)など基本的なHTTP認証プロトコル用
  "org.pac4j" % "pac4j-http" % pac4jVersion
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
