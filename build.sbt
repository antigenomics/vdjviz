name := "test"

version := "1.0-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += (
  "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"
  )

libraryDependencies ++= Seq(
  "com.antigenomics" % "vdjtools" % "1.0-SNAPSHOT",
  "ws.securesocial" %% "securesocial" % "2.1.4",
  filters,
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings
