scalaVersion := "2.11.8"
name := "gitwatch"
enablePlugins(PlayScala)

libraryDependencies += "com.typesafe.akka" %% "akka-agent" % "2.4.12"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
libraryDependencies += "commons-codec" % "commons-codec" % "1.10"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test"
libraryDependencies += ws
libraryDependencies += "net.sf.saxon" % "Saxon-HE" % "9.7.0-10"
libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.9.6"

publishArtifact in(Compile, packageDoc) := false
publishArtifact in packageDoc := false
sources in(Compile, doc) := Seq.empty
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
      interface <- Option(interface).collect { case i: com.typesafe.sbt.git.JGit => i }
      ref <- Option(interface.repo.resolve(sha))
      message <- {
        val walk = new org.eclipse.jgit.revwalk.RevWalk(interface.repo)
        try Option(walk.parseCommit(ref.toObjectId)).flatMap(commit => Option(commit.getFullMessage))
        finally walk.dispose()
      }
    } yield message
  }
}.map { str => java.util.Base64.getEncoder.encodeToString(str.getBytes("UTF-8")) }

buildInfoPackage := "gitwatch"
buildInfoOptions += BuildInfoOption.ToJson
lazy val gitCommitDescription = SettingKey[Option[String]]("gitCommitDescription", "Base64-encoded!")

enablePlugins(RpmPlugin)
rpmVendor := "gitwatch"
