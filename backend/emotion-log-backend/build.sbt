name := """emotion-log-backend"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

val playPac4jVersion = "12.0.2-PLAY3.0"
val pac4jVersion = "6.1.3"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
  jdbc,
  evolutions,
  // Anorm - PlayのシンプルなSQLデータアクセスライブラリ
  "org.playframework.anorm" %% "anorm" % "2.7.0",
  // SQLite JDBCドライバ
  "org.xerial" % "sqlite-jdbc" % "3.45.3.0",
  // play-pac4j
  "org.pac4j" %% "play-pac4j" % playPac4jVersion,
  "org.pac4j" % "pac4j-http" % pac4jVersion excludeAll (ExclusionRule(
    organization = "com.fasterxml.jackson.core"
  )),
  "org.pac4j" % "pac4j-jwt" % pac4jVersion exclude ("commons-io", "commons-io"),
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.19.1"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
