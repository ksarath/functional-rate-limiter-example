lazy val commonSettings = Seq(
  name         := "rate-limiter-example",
  version      := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.8"
)

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ypartial-unification",
  "-Ywarn-unused-import"
)

lazy val coreDependencies = Seq(
)

lazy val libDependencies = Seq(
  "org.systemfw" %% "upperbound" % "0.2.0-M2",
  "co.fs2" %% "fs2-core" % "1.0.2",
  "org.typelevel" %% "cats-core" % "1.6.0",
  "org.typelevel" %% "cats-effect" % "1.2.0",
  "org.typelevel" %% "cats-collections-core" % "0.7.0"
)

lazy val `rate-limiter-example` = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    dependencyOverrides ++= coreDependencies,
    libraryDependencies ++= libDependencies
  )