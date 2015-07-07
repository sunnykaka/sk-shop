import play.sbt.PlayImport._
import sbt._
import Keys._

object Dependencies {

  val mysqlConnectorVersion = "5.1.32"
  val springVersion = "4.1.6.RELEASE"

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
    "junit" % "junit-dep" % "4.8.1" % "test" exclude("org.hamcrest", "hamcrest-core"),
    //"joda-time" % "joda-time" % "2.6",
    "org.jadira.usertype" % "usertype.core" % "3.2.0.GA" exclude("junit", "junit"),
    //"org.apache.commons" % "commons-lang3" % "3.3.1",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate4" % "2.5.4",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.5.4",
    "com.typesafe.play" %% "play-mailer" % "3.0.1"
  )

  val playDependencies = Seq(
    javaCore,
    javaWs,
    cache,
    javaJdbc,
    "com.typesafe.play.modules" %% "play-modules-redis" % "2.4.1" exclude("junit", "junit")
  )

  val commonDependencies: Seq[ModuleID] = common ++ springHibernate ++ playDependencies

  val userDependencies: Seq[ModuleID] = Seq()

  val productDependencies: Seq[ModuleID] = Seq()

  val orderDependencies: Seq[ModuleID] = Seq("jdom" % "jdom" % "1.0")

  val adminDependencies: Seq[ModuleID] = Seq(mysqlConnector) ++ springTest ++ c3p0

  val shopDependencies: Seq[ModuleID] = adminDependencies


}