ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Ve-scala",
    libraryDependencies ++= Seq("com.atilika.kuromoji" % "kuromoji-ipadic" % "0.9.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.1",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.1" classifier "models",
      "org.slf4j" % "slf4j-api" % "2.0.3",
      "ch.qos.logback" % "logback-classic" % "1.4.4",
      "org.scalatest" %% "scalatest" % "3.2.14" % "test"
    )
  )
