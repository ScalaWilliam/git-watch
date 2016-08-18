scalaVersion := "2.11.8"
name := "gitwatch"
enablePlugins(PlayScala)

libraryDependencies += "com.typesafe.akka" %% "akka-agent" % "2.4.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
libraryDependencies += "commons-codec" % "commons-codec" % "1.10"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test"
libraryDependencies += ws

publishArtifact in(Compile, packageDoc) := false
publishArtifact in packageDoc := false
sources in(Compile, doc) := Seq.empty
