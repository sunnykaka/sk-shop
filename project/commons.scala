import sbt._
import Keys._

object Commons {
  val appVersion = "1.0.0-SNAPSHOT"
  val commonScalaVersion = "2.11.6"

  val settings: Seq[Def.Setting[_]] = Seq(
    version := appVersion,
    scalaVersion := commonScalaVersion,
    javacOptions ++= Seq("-encoding", "UTF-8"),
    resolvers ++= Seq(
      Opts.resolver.mavenLocalFile,
      Resolver.typesafeRepo("releases"),
      Resolver.sonatypeRepo("releases"),
      "local repository" at "http://192.168.1.100:8081/nexus/content/groups/public/"
    )
  )
}
