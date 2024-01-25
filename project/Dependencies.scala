import sbt._

object Dependencies {
  lazy val munit = "org.scalameta" %% "munit" % "0.7.29"

  lazy val tests = Seq(
    munit % Test
  )

  lazy val camel = Seq(
    "io.guanaco.alerta" %% "api" % "2.1.0-SNAPSHOT",
    "io.guanaco.alerta" %% "impl" % "2.1.0-SNAPSHOT" % Test,

    "org.apache.camel" % "camel-core" % "2.16.3",

    "junit" % "junit" % "4.11" % Test,
    "org.apache.camel" % "camel-test" % "2.16.3" % Test,
  )

  lazy val rest = Seq(
    "org.apache.cxf" % "crt-rt-frontend-jaxrs" % "3.1.5",
  )
}
