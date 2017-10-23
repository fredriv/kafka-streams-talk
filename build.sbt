lazy val root = (project in file(".")).
  settings(
    name         := "kafka-streams-talk",
    organization := "com.schibsted",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-streams" % "0.11.0.1",
      "org.slf4j" % "slf4j-simple" % "1.7.25",
      "org.scalatest" %% "scalatest" % "3.0.3" % Test
    )
  )
