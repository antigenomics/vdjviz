name := """vdjviz"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

resolvers += (
  "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"
  )

libraryDependencies ++= Seq(
  "com.antigenomics" % "vdjtools" % "1.0-SNAPSHOT",
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)
