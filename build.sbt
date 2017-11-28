name := "untitled"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
libraryDependencies += "net.liftweb" %% "lift-json" % "3.2.0-M3"
libraryDependencies += "com.typesafe" % "config" % "1.3.1"
logBuffered in Test := false