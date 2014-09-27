name := """vdjviz"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.10.2"

resolvers += (
  "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"
  )

//resolvers += Resolver.url("sbt-plugin-releases",new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "com.antigenomics" % "vdjtools" % "1.0-SNAPSHOT",
  //"com.fasterxml.jackson.core" % "jackson-databind" % "2.3.3",
  //"securesocial" %% "securesocial" % "2.1.2",
  filters,
  javaJdbc,
  javaEbean,
  "com.google.guava" % "guava" % "14.0",
  cache,
  javaWs
)


