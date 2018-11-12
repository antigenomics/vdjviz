name := "vdjviz"

version := "1.0.4"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += (
  "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"
  )

resolvers += (
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  "com.github.mikessh" % "vdjtools" % "1.2.1",
  "ws.securesocial" %% "securesocial" % "2.1.4",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0",
  "mysql" % "mysql-connector-java" % "5.1.18",
  filters,
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings
