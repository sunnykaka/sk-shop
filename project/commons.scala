import play.PlayImport.PlayKeys._
import sbt._
import Keys._

object Commons {
  val appVersion = "1.0.0-SNAPSHOT"
  val commonScalaVersion = "2.11.6"

  val settings: Seq[Def.Setting[_]] = Seq(
    version := appVersion,
    scalaVersion := commonScalaVersion,
    scalacOptions := Seq("-encoding", "UTF-8", "-Xlint", "-deprecation", "-unchecked", "-feature"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8", "-Xlint:-options"),
    resolvers ++= Seq(
      Opts.resolver.mavenLocalFile,
      //"local repository" at "http://192.168.1.100:8081/nexus/content/groups/public/",
      Resolver.typesafeRepo("releases"),
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      "pk11 repo" at "http://pk11-scratch.googlecode.com/svn/trunk"
    )
  )
}
