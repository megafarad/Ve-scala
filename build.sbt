ThisBuild / version := "0.2.0"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / crossPaths := false

lazy val commonDependencies = Seq("org.slf4j" % "slf4j-api" % "2.0.12",
  "ch.qos.logback" % "logback-classic" % "1.5.6",
  "org.scalatest" %% "scalatest" % "3.2.19" % "test")

lazy val global = (project in file("."))
  .settings(
    name := "ve-scala",
    publish / skip := true
  )
  .aggregate(core, service)
  .dependsOn(core)

lazy val core = (project in file("core"))
  .settings(
    name := "ve-scala-core",
    libraryDependencies ++= Seq("com.atilika.kuromoji" % "kuromoji-ipadic" % "0.9.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.7",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.7" classifier "models") ++ commonDependencies
  )

lazy val service = (project in file("service"))
  .settings(
    name := "ve-scala-service",
    publish / skip := true,
    libraryDependencies ++= commonDependencies ++ Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % PekkoVersion,
      "org.apache.pekko" %% "pekko-http" % PekkoHttpVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "com.auth0" % "jwks-rsa" % "0.22.1",
      "com.github.jwt-scala" %% "jwt-core" % "10.0.1",
      "com.github.jwt-scala" %% "jwt-circe" % "10.0.1",
      "com.github.pjfanning" %% "pekko-http-circe" % "2.6.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
    ),
    Docker / packageName := "ve-scala-service",
    dockerBaseImage := "amazoncorretto:19",
    dockerExposedPorts := Seq(9000)
  )
  .dependsOn(core)
  .enablePlugins(JavaAppPackaging, DockerPlugin)


val PekkoVersion = "1.0.2"
val PekkoHttpVersion = "1.0.1"
val circeVersion = "0.14.9"