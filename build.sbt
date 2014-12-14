name := "vdjviz"

version := "0.8-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += (
  "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"
  )

libraryDependencies ++= Seq(
  "com.antigenomics" % "vdjtools" % "1.0-SNAPSHOT",
  "com.antigenomics" % "vdjdb" % "1.0-SNAPSHOT",
  "ws.securesocial" %% "securesocial" % "2.1.4",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0",
  "mysql" % "mysql-connector-java" % "5.1.18",
  filters,
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings
