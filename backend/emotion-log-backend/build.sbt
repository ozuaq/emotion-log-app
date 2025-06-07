name := """emotion-log-backend"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
libraryDependencies += jdbc
libraryDependencies += evolutions
// Anorm - PlayのシンプルなSQLデータアクセスライブラリ
libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.7.0"
// SQLite JDBCドライバ
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.45.3.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
