import sbt._

object Builds extends sbt.Build {
  import Keys._
  import ls.Plugin.{lsSettings,LsKeys}

  val g8version = "0.4.0"

  lazy val buildSettings = Defaults.defaultSettings ++ lsSettings ++ Seq(
    organization := "net.databinder.giter8",
    version := g8version,
    scalaVersion := "2.9.1",
    libraryDependencies ++= Seq(
      "org.clapper" %% "scalasti" % "0.5.5"),
    publishArtifact in (Compile, packageBin) := true,
    homepage :=
      Some(url("https://github.com/n8han/giter8")),
    publishMavenStyle := true,
    publishTo :=
      Some("releases" at
           "https://oss.sonatype.org/service/local/staging/deploy/maven2"),
    publishArtifact in Test := false,
    licenses := Seq("LGPL v3" -> url("http://www.gnu.org/licenses/lgpl.txt")),
    pomExtra := (
      <scm>
        <url>git@github.com:n8han/giter8.git</url>
        <connection>scm:git:git@github.com:n8han/giter8.git</connection>
      </scm>
      <developers>
        <developer>
          <id>n8han</id>
          <name>Nathan Hamblen</name>
          <url>http://twitter.com/n8han</url>
        </developer>
      </developers>)
  )
  
  // posterous title needs to be giter8, so both app and root are named giter8
  lazy val root = Project("root", file("."),
    settings = buildSettings ++ Seq(
      name := "giter8",
      LsKeys.skipWrite := true
    )) aggregate(plugin, app, lsLibrary)

  lazy val app = Project("app", file("app"),
    settings = buildSettings ++ conscript.Harness.conscriptSettings ++ Seq(
      description :=
        "Command line tool to apply templates defined on github",
      version in lsLibrary <<= version,
      organization in lsLibrary := "net.databinder.giter8",
      publishTo in lsLibrary <<= publishTo,
      publishMavenStyle in lsLibrary := true,
      name := "giter8",
      libraryDependencies +=
        "net.databinder" %% "dispatch-lift-json" % "0.8.5"
    )) dependsOn (lsLibrary)

  lazy val plugin = Project("giter8-plugin", file("plugin"),
    settings = buildSettings ++ Seq(
      description :=
        "sbt 0.11 plugin for testing giter8 templates",
      sbtPlugin := true,
      libraryDependencies <++= (sbtDependency, sbtVersion) { (sd, sv) =>
        Seq(sd,
            "org.scala-tools.sbt" %% "scripted-plugin" % sv
            )
      }
    ))
  lazy val lsLibrary =
    ProjectRef(uri("git://github.com/softprops/ls.git#d32a4d7"), "library")
}
