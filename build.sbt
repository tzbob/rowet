organization := "be.tzbob"
scalaVersion := "3.1.3"
version      := "0.0.1-SNAPSHOT"
name         := "rowet"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-deprecation",
  "-language:higherKinds",
  "-Ykind-projector",
  "-new-syntax",
  "-indent",
  "-Yexplicit-nulls",
  "-Ysafe-init"
)

name := "rowet"

Global / onChangedBuildSource := ReloadOnSourceChanges

libraryDependencies ++= Seq(
  "org.typelevel"   %% "cats-effect"  % "3.3.3",
  "net.java.dev.jna" % "jna"          % "5.10.0",
  "net.java.dev.jna" % "jna-platform" % "5.10.0",
  "org.scalatest"   %% "scalatest"    % "3.2.10" % "test"
)
