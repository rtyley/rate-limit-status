organization := "com.madgag"

name := "rate-limit-status"

description := "For understanding API quota consumption"

scalaVersion := "2.12.6"

scmInfo := Some(ScmInfo(
  url("https://github.com/rtyley/rate-limit-status"),
  "scm:git:git@github.com:rtyley/rate-limit-status.git"
))

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

publishTo := sonatypePublishTo.value

import ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)
