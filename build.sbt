import ReleaseTransformations.*
import sbtversionpolicy.withsbtrelease.ReleaseVersion

organization := "com.madgag"

name := "rate-limit-status"

description := "For understanding API quota consumption"

scalaVersion := "2.13.16"

crossScalaVersions := Seq(scalaVersion.value, "2.12.20", "3.3.4")

licenses := Seq(License.Apache2)

scalacOptions ++= Seq("-deprecation", "-release:11")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test

releaseVersion := ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease().value
releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion
)
