name := """HMS"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.1",
  "commons-io" % "commons-io" % "2.4",
  "org.assertj" % "assertj-core" % "2.2.0",
  "org.seleniumhq.selenium" % "selenium-java" % "2.48.2",
  javaWs,
  filters
)

resolvers ++= Seq(
  "Apache" at "http://repo1.maven.org/maven2/",
  "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
  "Sonatype OSS Snasphots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)