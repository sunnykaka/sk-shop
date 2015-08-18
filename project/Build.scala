import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.web.Import._
import sbt._
import Keys._
import Dependencies._

object ApplicationBuild extends Build {

  lazy val common = (project in file("common")).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= commonDependencies,
      unmanagedBase := baseDirectory.value / "lib"
    )

  lazy val user = (project in file("user-center")).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= userDependencies
    ).
    dependsOn(common)

  lazy val product = (project in file("product-center")).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= productDependencies
    ).
    dependsOn(common).dependsOn(user)

  lazy val order = (project in file("order-center")).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= orderDependencies
    ).
    dependsOn(common).dependsOn(product).dependsOn(user)

/*
  lazy val admin = (project in file("admin")).
    enablePlugins(play.PlayJava).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= adminDependencies,
      sourceGenerators in Compile += task {
        val dir: File = (sourceManaged in Compile).value / "controllers"
        val dirs = Seq(dir / "ref", dir / "javascript")
        dirs.foreach(_.mkdirs)
        Seq[File]()
      },
      unmanagedSourceDirectories in Compile += (sourceManaged in Compile).value
//      unmanagedSourceDirectories in Compile += baseDirectory.value / "target" / "scala-2.11" / "src_managed" / "main"
    ).dependsOn(common % "test->test;compile->compile").
      dependsOn(user).dependsOn(product).dependsOn(order)
*/

  lazy val shop = webProject("shop", shopDependencies)

  lazy val app = webProject("app", appDependencies)

  def webProject(name: String, dependencies: Seq[ModuleID]) = {
    Project(name, file(name)).
    enablePlugins(play.sbt.PlayJava).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= dependencies,
      sourceGenerators in Compile += task {
        val dir: File = (sourceManaged in Compile).value / "controllers"
        val dirs = Seq(dir / "ref", dir / "javascript")
        dirs.foreach(_.mkdirs)
        Seq[File]()
      },
      unmanagedSourceDirectories in Compile += (sourceManaged in Compile).value,
      pipelineStages := Seq(digest, gzip)
    ).dependsOn(common % "test->test;compile->compile").
    dependsOn(user).dependsOn(product).dependsOn(order)
  }

  lazy val root = (project in file(".")).
    settings(Commons.settings: _*).
    aggregate(shop, app)
}
