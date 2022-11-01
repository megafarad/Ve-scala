ThisBuild / organization := "com.megafarad"
ThisBuild / organizationName := "Megafarad"
ThisBuild / organizationHomepage := Some(url("http://megafarad.com/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/megafarad/Ve-scala"),
    "scm:git@github.com:megafarad/Ve-scala.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "SirHC1977",
    name = "Chris Carrington",
    email = "chris@megafarad.com",
    url = url("https://megafarad.com")
  )
)

ThisBuild / description := "A Scala port of Kim Ahlström's Ve (https://github.com/Kimtaro/ve)"
ThisBuild / licenses := List(
  "MIT" -> new URL("https://opensource.org/licenses/MIT")
)
ThisBuild / homepage := Some(url("https://github.com/megafarad/Ve-scala"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  // For accounts created after Feb 2021:
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true