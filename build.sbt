name := """play-mdc-propagation"""
organization := "jsw"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  ws,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime,
  "com.markatta" %% "futiles" % "2.0.0"
)

