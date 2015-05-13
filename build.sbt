name := "vdjviz"

version := "1.0-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += (
  "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"
  )

resolvers += (
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )

libraryDependencies ++= Seq(
  "com.antigenomics" % "vdjtools" % "1.0-SNAPSHOT",
  "com.antigenomics" % "vdjdb" % "1.0-SNAPSHOT",
  "com.milaboratory" % "milib" % "1.0-SNAPSHOT",
  "ws.securesocial" %% "securesocial" % "2.1.4",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.mapdb" % "mapdb" % "1.0.7",
  filters,
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings
