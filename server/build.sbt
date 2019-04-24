name := """Covoituliege"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava).
  enablePlugins(JavaUnidocPlugin).enablePlugins(GenJavadocPlugin)

scalaVersion := "2.12.8"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

//ws client for testing
libraryDependencies += ws

// Java driver for mongoDB
libraryDependencies ++= Seq(
    "org.mongodb" % "mongodb-driver" % "3.8.2"
)

// Java commons math for matrix and FFT
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"


// JSon parsing
libraryDependencies += "org.glassfish" % "javax.json" % "1.0.4"

//mailing
libraryDependencies += "com.typesafe.play" %% "play-mailer" % "6.0.1"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "6.0.1"

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

//documentation generation setting.
javacOptions in (Compile,doc) += "-linksource"
javacOptions in (Compile,doc) += "-private"
javacOptions in (Compile,doc) ++= Seq("-link","https://docs.oracle.com/en/java/javase/11/docs/api/")
javacOptions in (Compile,doc) ++= Seq("-link","http://mongodb.github.io/mongo-java-driver/3.8/javadoc/")
javacOptions in (Compile,doc) ++= Seq("-link","https://commons.apache.org/proper/commons-math/javadocs/api-3.3/")
javacOptions in (Compile,doc) ++= Seq("-link","https://www.playframework.com/documentation/2.7.x/api/java/")






