scalaVersion := "2.11.8"
name := "gitwatch"
enablePlugins(PlayScala)
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
libraryDependencies += "commons-codec" % "commons-codec" % "1.10"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test"
publishArtifact in(Compile, packageDoc) := false
publishArtifact in packageDoc := false
sources in(Compile, doc) := Seq.empty
