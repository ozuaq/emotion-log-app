name := """emotion-log-backend"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  guice,
  // JDBC接続プール (HikariCPはPlayにデフォルトで含まれることが多い)
  jdbc,
  // Anorm - PlayのシンプルなSQLデータアクセスライブラリ
  "org.playframework.anorm" %% "anorm" % "2.7.0", // Play 2.9と互換性のあるAnormのバージョンを確認
  // SQLite JDBCドライバ
  "org.xerial" % "sqlite-jdbc" % "3.45.3.0", // 最新安定版を確認してください
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
  // filters, // Play 2.9では通常不要
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
