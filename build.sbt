organization := "be.tzbob"
scalaVersion := "3.1.0"
version := "0.0.1-SNAPSHOT"
name := "rowet"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-deprecation",
  "-language:higherKinds",
  "-Ykind-projector",
  "-new-syntax",
  "-indent"
)

name := "rowet"

Global / onChangedBuildSource := ReloadOnSourceChanges

libraryDependencies ++= Seq(
  "org.typelevel"    %% "cats-effect" % "3.3.3",
  "net.java.dev.jna" % "jna"          % "4.5.1",
  "net.java.dev.jna" % "jna-platform" % "4.5.1",
  "org.scalatest"    %% "scalatest"   % "3.2.10" % "test"
)
