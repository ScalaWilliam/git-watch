scalaVersion := "2.12.1"
name := "gitwatch"
enablePlugins(PlayScala)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.0-M5"
libraryDependencies += "com.typesafe.play" %% "play-iteratees" % "2.6.1"
libraryDependencies += "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1"
publishArtifact in (Compile, packageDoc) := false
publishArtifact in packageDoc := false
sources in (Compile, doc) := Seq.empty
enablePlugins(BuildInfoPlugin)

buildInfoKeys := Seq[BuildInfoKey](
  name,
  version,
  scalaVersion,
  sbtVersion,
  buildInfoBuildNumber,
  git.gitHeadCommit,
  gitCommitDescription
)

gitCommitDescription := {
  com.typesafe.sbt.SbtGit.GitKeys.gitReader.value.withGit { interface =>
    for {
      sha <- git.gitHeadCommit.value
      interface <- Option(interface).collect {
        case i: com.typesafe.sbt.git.JGit => i
      }
      ref <- Option(interface.repo.resolve(sha))
      message <- {
        val walk = new org.eclipse.jgit.revwalk.RevWalk(interface.repo)
        try Option(walk.parseCommit(ref.toObjectId)).flatMap(commit =>
          Option(commit.getFullMessage))
        finally walk.dispose()
      }
    } yield message
  }
}.map { str =>
  java.util.Base64.getEncoder.encodeToString(str.getBytes("UTF-8"))
}

buildInfoPackage := "gitwatch"
buildInfoOptions += BuildInfoOption.ToJson
lazy val gitCommitDescription =
  SettingKey[Option[String]]("gitCommitDescription", "Base64-encoded!")
