import sbt._

object Dependencies {

  object Versions {

    val camelBinary   = "3-9"
    val camel         = "3.9.0"
    val slf4j         = "1.7.13"
    val scalaTest     = "3.0.4"
    val activemq      = "5.13.3"
    val cxf           = "3.1.5"
    val easymock      = "3.4"
    val guanacoAlerta = "2.0.7-SNAPSHOT"
    val jaxb          = "2.3.1"
  }

  lazy val slf4jApi      = "org.slf4j"           % "slf4j-api"      % Versions.slf4j
  lazy val slf4jLog4j    = "org.slf4j"           % "slf4j-log4j12"  % Versions.slf4j
  lazy val camelCore     = "org.apache.camel"    % "camel-core"     % Versions.camel
  lazy val camelQuartz   = "org.apache.camel"    % "camel-quartz"   % Versions.camel
  lazy val activeMQCamel = "org.apache.activemq" % "activemq-camel" % Versions.activemq

  lazy val cxfFrontend = "org.apache.cxf" % "cxf-rt-frontend-jaxrs"   % Versions.cxf
  lazy val cxfSecurity = "org.apache.cxf" % "cxf-rt-rs-security-cors" % Versions.cxf
  lazy val jaxbApi     = "javax.xml.bind" % "jaxb-api"                % Versions.jaxb

  lazy val alertaApi  = "io.guanaco.alerta" %% "api"  % Versions.guanacoAlerta
  lazy val alertaUtil = "io.guanaco.alerta" %% "util" % Versions.guanacoAlerta
  lazy val alertaImpl = "io.guanaco.alerta" %% "impl" % Versions.guanacoAlerta
  lazy val alertaTest = "io.guanaco.alerta" %% "test" % Versions.guanacoAlerta

  lazy val camelTest = "org.apache.camel" % "camel-test" % Versions.camel
  lazy val scalaTest = "org.scalatest"    %% "scalatest" % Versions.scalaTest
  lazy val easyMock  = "org.easymock"     % "easymock"   % Versions.easymock

  lazy val Camel = Seq(
    alertaApi,
    camelCore,
    camelQuartz,
    activeMQCamel,
    alertaImpl % Test,
    slf4jLog4j % Test,
    easyMock   % Test,
    camelTest  % Test
  )

  lazy val Rest = Seq(
    slf4jApi,
    jaxbApi,
    cxfFrontend,
    cxfSecurity
  )

}
