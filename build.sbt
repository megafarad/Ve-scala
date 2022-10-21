ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Ve-scala",
    libraryDependencies ++= Seq("com.atilika.kuromoji" % "kuromoji-ipadic" % "0.9.0",
      "org.scalatest" %% "scalatest" % "3.2.14" % "test",
      "org.scalatestplus" %% "mockito-4-6" % "3.2.14.0" % "test"
    )
  )
