ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val commonDependencies = Seq("org.slf4j" % "slf4j-api" % "2.0.3",
  "ch.qos.logback" % "logback-classic" % "1.4.4",
  "org.scalatest" %% "scalatest" % "3.2.14" % "test")

lazy val core = (project in file("core"))
  .settings(
    libraryDependencies ++= Seq("com.atilika.kuromoji" % "kuromoji-ipadic" % "0.9.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.1",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.1" classifier "models") ++ commonDependencies
  )

lazy val service = (project in file("service"))
  .settings(
    libraryDependencies ++= commonDependencies ++ Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "de.heikoseeberger" %% "akka-http-circe" % "1.40.0-RC3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
    ),
    Docker / packageName := "ve-scala-service",
    dockerBaseImage := "amazoncorretto:19",
    dockerExposedPorts := Seq(9000)
  )
  .dependsOn(core)
  .enablePlugins(JavaAppPackaging, DockerPlugin)


val AkkaVersion = "2.6.20"
val AkkaHttpVersion = "10.2.10"
val circeVersion = "0.14.3"