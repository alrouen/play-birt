name := """play-birt"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.eclipse.birt.runtime" % "org.eclipse.birt.runtime" % "4.2.2" exclude("org.milyn", "flute") exclude("milyn", "flute")
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)
