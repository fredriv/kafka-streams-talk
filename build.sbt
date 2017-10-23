lazy val root = (project in file(".")).
  settings(
    name         := "kafka-streams-talk",
    organization := "com.schibsted",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.3" % Test
    )
  )
