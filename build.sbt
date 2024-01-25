import sbt.KeyRanks.ATask
import sbt.file

lazy val scala213               = "2.13.5"
lazy val scala212               = "2.12.14"
lazy val supportedScalaVersions = List(scala213, scala212)

ThisBuild / scalaVersion := scala212
ThisBuild / version := "1.1.0-SNAPSHOT"
ThisBuild / organization := "io.guanaco.scheduler"
ThisBuild / organizationName := "Guanaco"

val commonSettings = Seq(
  publishMavenStyle := true,
  githubOwner := "guanaco-io",
  githubRepository := "scheduler",
  githubTokenSource := TokenSource.Environment("GITHUB_TOKEN")
)

lazy val root = (project in file("."))
  .enablePlugins(ScalafmtPlugin, JavaAppPackaging, SbtOsgi)
  .settings(commonSettings)
  .settings(
    name := "scheduler",
    libraryDependencies ++= Dependencies.tests,
    crossScalaVersions := Nil,
    publish / skip := true
  )
  .aggregate(api, camel, rest)

lazy val api = (project in file("api"))
  .enablePlugins(SbtOsgi)
  .settings(commonSettings)
  .settings(
    name := "api",
    description := "Alerta public API",
    crossScalaVersions := supportedScalaVersions,
    osgiSettings,
    OsgiKeys.exportPackage := List(OsgiKeys.bundleSymbolicName.value),
    OsgiKeys.privatePackage := Nil,
    OsgiKeys.additionalHeaders := Map(
      "Bundle-Name" -> "Guanaco :: Alerta :: API"
    )
  )

val packageXml       = taskKey[File]("Produces an xml artifact.").withRank(ATask)
val generateFeatures = taskKey[Unit]("Generates the features files.")

lazy val features = (project in file("features"))
  .settings(commonSettings)
  .settings(
    generateFeatures := {
      streams.value.log.info("Generating features.xml files")
      val input  = (resourceDirectory in Compile).value / "features.xml"
      val output = file("features") / "target" / "features.xml"
      IO.write(output, IO.read(input).replaceAll("\\$\\{version\\}", version.value))
    },
    publishM2 := (publishM2 dependsOn generateFeatures).value,
    publish := (publish dependsOn generateFeatures).value,
    name := "features",
    crossScalaVersions := Nil,
    // disable .jar publishing
    publishArtifact in (Compile, packageBin) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false,
    packageXml := file("features") / "target" / "features.xml",
    addArtifact(Artifact("features", "features", "xml"), packageXml).settings
  )

lazy val camel = (project in file("camel"))
  .enablePlugins(SbtOsgi)
  .settings(commonSettings)
  .settings(
    name := "impl",
    description := "Camel routes from MQ to Alerta API",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Dependencies.camel,
    parallelExecution in Test := false,
    osgiSettings,
    OsgiKeys.importPackage := List("*", "org.apache.activemq.camel.component"),
    OsgiKeys.privatePackage := List(s"${OsgiKeys.bundleSymbolicName.value}.*", "spray.json"),
    OsgiKeys.additionalHeaders := Map(
      "Bundle-Name" -> "Guanaco :: Alerta :: Implementation"
    )
  )
  .dependsOn(api)

lazy val rest = (project in file("rest"))
  .settings(commonSettings)
  .settings(
    name := "test",
    description := "Utilities for unit testing your own alerta projects",
    libraryDependencies ++= Dependencies.rest,
    crossScalaVersions := supportedScalaVersions
  )
  .dependsOn(api)

scalacOptions += "-feature"
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a"))

fork in run := true
