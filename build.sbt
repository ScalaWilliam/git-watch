scalaVersion := "2.12.1"
name := "gitwatch"
enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest"                       % "3.0.1" % "test",
  "com.typesafe.play" %% "play-json"                       % "2.6.0-M5",
  "com.typesafe.play" %% "play-iteratees"                  % "2.6.1",
  "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1"
)

publishArtifact in (Compile, packageDoc) := false
publishArtifact in packageDoc := false
sources in (Compile, doc) := Seq.empty
