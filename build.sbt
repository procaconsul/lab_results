name := "untitled"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
logBuffered in Test := false