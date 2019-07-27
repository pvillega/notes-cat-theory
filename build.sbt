// *****************************************************************************
// Projects
// *****************************************************************************
resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.scalamacros" % "paradise"        % "2.1.1" cross CrossVersion.full)
addCompilerPlugin("org.spire-math"  %% "kind-projector" % "0.9.10")

lazy val `practical-cat-theory` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
          library.cats,
          library.catsEffect,
          library.discipline,
          library.droste,
          library.drosteMacros,
          library.monocleCore,
          library.monocleMacro,
          library.shapeless,
          library.catsLaws      % Test,
          library.monocleLaw    % Test,
          library.scalaCheck    % Test,
          library.scalaTest     % Test
        )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val cats          = "1.6.1"
      val catsEffect    = "1.3.1"
      val discipline    = "0.10.0"
      val droste        = "0.7.0"
      val monocle       = "1.6.0"
      val shapeless     = "2.3.3"
      val scalaCheck    = "1.13.5"
      val scalaTest     = "3.0.1"
    }
    val cats          = "org.typelevel"              %% "cats-core"                % Version.cats
    val catsLaws      = "org.typelevel"              %% "cats-laws"                % Version.cats
    val catsEffect    = "org.typelevel"              %% "cats-effect"              % Version.catsEffect
    val discipline    = "org.typelevel"              %% "discipline"               % Version.discipline
    val droste        = "io.higherkindness"          %% "droste-core"              % Version.droste
    val drosteMacros  = "io.higherkindness"          %% "droste-macros"            % Version.droste
    val monocleCore   = "com.github.julien-truffaut" %% "monocle-core"             % Version.monocle
    val monocleMacro  = "com.github.julien-truffaut" %% "monocle-macro"            % Version.monocle
    val monocleLaw    = "com.github.julien-truffaut" %% "monocle-law"              % Version.monocle
    val shapeless     = "com.chuusai"                %% "shapeless"                % Version.shapeless
    val scalaCheck    = "org.scalacheck"             %% "scalacheck"               % Version.scalaCheck
    val scalaTest     = "org.scalatest"              %% "scalatest"                % Version.scalaTest
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
        "-encoding",
        "UTF-8",
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
