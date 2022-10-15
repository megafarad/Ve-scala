ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Ve-scala",
    libraryDependencies ++= Seq("com.atilika.kuromoji" % "kuromoji-ipadic" % "0.9.0")
  )
