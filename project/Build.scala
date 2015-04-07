import sbt._
import Keys._
import play.Play.autoImport._
import Dependencies._

object ApplicationBuild extends Build {

  lazy val common = (project in file("common")).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= commonDependencies
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
    dependsOn(common)

  lazy val order = (project in file("order-center")).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= orderDependencies
    ).
    dependsOn(common)


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

  lazy val root = (project in file(".")).
    settings(Commons.settings: _*).
    aggregate(admin)
}
