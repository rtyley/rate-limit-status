organization := "com.madgag"

name := "rate-limit-status"

description := "For understanding API quota consumption"

scalaVersion := "2.13.1"

crossScalaVersions := Seq(scalaVersion.value, "2.12.10", "2.11.12")

scmInfo := Some(ScmInfo(
  url("https://github.com/rtyley/rate-limit-status"),
  "scm:git:git@github.com:rtyley/rate-limit-status.git"
))

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

publishTo := sonatypePublishToBundle.value

import ReleaseTransformations._

releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
