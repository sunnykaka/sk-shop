import play.PlayImport._
import sbt._
import Keys._

object Dependencies {

  val mysqlConnectorVersion = "5.1.32"
  val springVersion = "4.1.1.RELEASE"

  val mysqlConnector = "mysql" % "mysql-connector-java" % mysqlConnectorVersion
  val h2Connector = "com.h2database" % "h2" % "1.4.181"

  val springHibernate = Seq(
    "org.springframework" % "spring-context" % springVersion,
    "org.springframework" % "spring-orm" % springVersion,
    "org.springframework" % "spring-jdbc" % springVersion,
    "org.springframework" % "spring-tx" % springVersion,
    "org.springframework" % "spring-expression" % springVersion,
    "org.springframework" % "spring-aop" % springVersion,
    "org.hibernate" % "hibernate-entitymanager" % "4.3.6.Final"
  )

  val springTest = Seq(
    "org.springframework" % "spring-test" % springVersion % "test" exclude("junit", "junit")
  )

  val c3p0 = Seq("c3p0" % "c3p0" % "0.9.1.2")
  val common = Seq(
    "joda-time" % "joda-time" % "2.6",
    "org.jadira.usertype" % "usertype.core" % "3.2.0.GA" exclude("junit", "junit"),
    "org.apache.commons" % "commons-lang3" % "3.3.1",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate4" % "2.2.3",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.4.4",
    "com.typesafe.play" %% "play-mailer" % "2.4.0"
  )

  val playDependencies = Seq(
    javaCore,
    javaWs  % "test",
    cache,
    //"com.typesafe.play.plugins" %% "play-plugins-redis" % "2.3.1"
    "biz.source_code" %  "base64coder" % "2010-12-19",
    "org.sedis" %% "sedis" % "1.2.2"

  )

  val commonDependencies: Seq[ModuleID] = common ++ springHibernate ++ playDependencies

  val userDependencies: Seq[ModuleID] = Seq()

  val productDependencies: Seq[ModuleID] = Seq()

  val orderDependencies: Seq[ModuleID] = Seq("jdom" % "jdom" % "1.0")

  val adminDependencies: Seq[ModuleID] = Seq(mysqlConnector) ++ springTest ++ c3p0

  val shopDependencies: Seq[ModuleID] = adminDependencies


}