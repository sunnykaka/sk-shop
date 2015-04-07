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
    "org.springframework" % "spring-test" % springVersion % "test" exclude("junit", "junit"),
    "org.hamcrest" % "hamcrest-all" % "1.3" % "test"
  )

  val c3p0 = Seq("c3p0" % "c3p0" % "0.9.1.2")
  val common = Seq(
    "joda-time" % "joda-time" % "2.6",
    "org.jadira.usertype" % "usertype.core" % "3.2.0.GA" exclude("junit", "junit"),
    "org.apache.commons" % "commons-lang3" % "3.3.1"
  )

  val playDependencies = Seq(
    javaCore exclude("org.hamcrest", "hamcrest-core"),
    javaWs % "test"
  )

  val commonDependencies: Seq[ModuleID] = common ++ springHibernate ++ playDependencies

  val userDependencies: Seq[ModuleID] = Seq()

  val productDependencies: Seq[ModuleID] = Seq()

  val orderDependencies: Seq[ModuleID] = Seq()

  val adminDependencies: Seq[ModuleID] = Seq(mysqlConnector) ++ springTest ++ c3p0


}