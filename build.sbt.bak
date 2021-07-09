import sbt.file

lazy val scala213               = "2.13.6"
lazy val supportedScalaVersions = List(scala213)

ThisBuild / scalaVersion := scala213
ThisBuild / version := "1.0.2-SNAPSHOT"
ThisBuild / organization := "io.guanaco.scheduler"
ThisBuild / organizationName := "Guanaco"

val commonSettings = Seq(
  publishMavenStyle := true,
  githubOwner := "guanaco-io",
  githubRepository := "scheduler",
  githubTokenSource := TokenSource.Environment("GITHUB_TOKEN")
)

lazy val root = (project in file("."))
  .enablePlugins(SbtOsgi)
  .settings(commonSettings)
  .settings(
    name := "scheduler",
    crossScalaVersions := Nil,
    publish / skip := true
  )
  .aggregate(api, camel, rest)

lazy val api = (project in file("api"))
  .enablePlugins(SbtOsgi)
  .settings(commonSettings)
  .settings(
    name := "api",
    description := "Scheduler public API",
    crossScalaVersions := supportedScalaVersions,
    osgiSettings,
    OsgiKeys.exportPackage := List("io.guanaco.scheduler"),
    OsgiKeys.privatePackage := Nil,
    OsgiKeys.additionalHeaders := Map(
      "Bundle-Name" -> "Guanaco :: Scheduler :: API"
    )
  )

lazy val camel = (project in file("camel"))
  .settings(commonSettings)
  .settings(
    name := "camel",
    description := "Camel route builders",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Dependencies.Camel
  )
  .dependsOn(api)

lazy val rest = (project in file("rest"))
  .enablePlugins(SbtOsgi)
  .settings(commonSettings)
  .settings(
    name := "rest",
    description := "REST interface exposing an API to all registered scheduled tasks",
    libraryDependencies ++= Dependencies.Rest,
    crossScalaVersions := supportedScalaVersions,
    osgiSettings,
    OsgiKeys.exportPackage := List(OsgiKeys.bundleSymbolicName.value),
    OsgiKeys.privatePackage := Nil,
    OsgiKeys.additionalHeaders := Map(
      "Bundle-Name" -> "Guanaco :: Scheduler :: REST"
    )
  )
  .dependsOn(api)

scalacOptions += "-feature"
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a"))

fork in run := true
