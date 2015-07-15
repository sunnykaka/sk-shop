import play.sbt.PlayImport._
import sbt._
import Keys._

object Dependencies {

  val mysqlConnectorVersion = "5.1.32"
  val springVersion = "4.1.6.RELEASE"

  val mysqlConnector = "mysql" % "mysql-connector-java" % mysqlConnectorVersion

  val kamonVersion = "0.4.0"

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

  val common = Seq(
    "junit" % "junit" % "4.11" % "test" exclude("org.hamcrest", "hamcrest-core"),
    "org.jadira.usertype" % "usertype.core" % "3.2.0.GA" exclude("junit", "junit"),
    "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate4" % "2.5.4",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.5.4",
    "com.typesafe.play" %% "play-mailer" % "3.0.1"
  )

  val playDependencies = Seq(
    javaCore,
    javaWs,
    cache,
    javaJdbc,
    "com.typesafe.play.modules" %% "play-modules-redis" % "2.4.1" exclude("junit", "junit"),
    "io.kamon" %% "kamon-core" % kamonVersion,
    "io.kamon" %% "kamon-statsd" % kamonVersion,
    "io.kamon" %% "kamon-system-metrics" % kamonVersion
  )

  val commonDependencies: Seq[ModuleID] = common ++ springHibernate ++ playDependencies

  val userDependencies: Seq[ModuleID] = Seq()

  val productDependencies: Seq[ModuleID] = Seq()

  val orderDependencies: Seq[ModuleID] = Seq("jdom" % "jdom" % "1.0")

  val adminDependencies: Seq[ModuleID] = Seq(mysqlConnector) ++ springTest

  val shopDependencies: Seq[ModuleID] = adminDependencies


}