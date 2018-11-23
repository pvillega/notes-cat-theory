// *****************************************************************************
// Projects
// *****************************************************************************
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")

lazy val `practical-cat-theory` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.cats,
        library.catsEffect,
        library.scalaCheck % Test,
        library.scalaTest      % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val cats = "1.2.0"
      val catsEffect = "1.0.0-RC2"
      val scalaCheck = "1.13.5"
      val scalaTest = "3.0.5"
    }
    val cats       = "org.typelevel"  %% "cats-core"   % Version.cats
    val catsEffect = "org.typelevel"  %% "cats-effect" % Version.catsEffect
    val scalaCheck = "org.scalacheck" %% "scalacheck"  % Version.scalaCheck
    val scalaTest  = "org.scalatest"  %% "scalatest"   % Version.scalaTest
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  scalafmtSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    // scalaVersion := "2.12.4",
    organization := "com.aracon",
    organizationName := "Pere Villega",
    startYear := Some(2018),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-Ypartial-unification",
      "-Ywarn-unused-import",
      "-Xfatal-warnings"
    ),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )
