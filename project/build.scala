import sbt._
import sbt.Keys._

object ArcheryBuild extends Build {

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "1.9.1"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.10.0"

  override lazy val settings = super.settings ++ Seq(
    organization := "com.meetup",
    scalaVersion := "2.10.2",
    licenses := Seq("BSD-style" -> url("http://opensource.org/licenses/MIT")),
    homepage := Some(url("http://github.com/meetup/archery")),

    scalacOptions ++= Seq(
      //"-no-specialization", // use this to build non-specialized jars
      "-Yinline-warnings",
      "-deprecation",
      "-unchecked",
      "-optimize",
      "-language:_",
      "-feature"
    )
  )

  lazy val noPublish = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

  // core project
  lazy val core = Project("core", file("core")).
    settings(coreSettings: _*)

  lazy val coreSettings = Seq(
    name := "archery",
    libraryDependencies ++= Seq(
      scalaTest % "test",
      scalaCheck % "test"
    )
  )

  // benchmark project
  lazy val benchmark = Project("benchmark", file("benchmark")).
    settings(benchmarkSettings: _*).
    dependsOn(core)

  lazy val benchmarkSettings = Seq(
    name := "spire-benchmark",

    fork in run := true,

    javaOptions in run += "-Xmx4G",

    libraryDependencies ++= Seq(
      "ichi.bench" % "thyme" % "0.1.1" from "http://plastic-idolatry.com/jars/thyme-0.1.1.jar",
      "com.azavea.geotrellis" %% "geotrellis" % "0.8.2-RC2"
    ),

    resolvers += Resolver.sonatypeRepo("releases")

  ) ++ noPublish

  // aggregate top-level project
  lazy val aggregate = Project("aggregate", file(".")).
    aggregate(core, benchmark).
    settings(aggregateSettings: _*)

  lazy val aggregateSettings = Seq(
    name := "archery-aggregate"
  ) ++ noPublish
}
