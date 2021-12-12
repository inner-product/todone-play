name := "todone"

Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / scalaVersion := "2.13.7"
ThisBuild / useSuperShell := false

// ScalaFix configuration
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

val catsVersion = "2.2.0"
val circeVersion = "0.13.0"

val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core"     % catsVersion,
    "io.circe"      %%% "circe-core"    % circeVersion,
    "io.circe"      %%% "circe-generic" % circeVersion,
    "io.circe"      %%% "circe-parser"  % circeVersion,
    "org.scalameta" %%% "munit"         % "0.7.17" % Test
  ),
  scalacOptions ++= Seq(
    "-Yrangepos",
    "-Ymacro-annotations",
    "-Werror"
  ),
  testFrameworks += new TestFramework("munit.Framework")
)

val deploy = taskKey[Unit]("Build and deploy the frontend to the backend asset location")
val build = taskKey[Unit]("Format, compile, and test")

lazy val data = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("data"))
  .settings(
    sharedSettings,
    build := {
      Def.sequential(scalafixAll.toTask(""), scalafmtAll, Test / test).value
    }
  )

lazy val backend = project
  .in(file("backend"))
  .settings(
    sharedSettings,
    libraryDependencies += guice,
    build := {
      Def.sequential(scalafixAll.toTask(""), scalafmtAll, Test / test).value
    }
  )
  .dependsOn(data.jvm)
  .enablePlugins(PlayScala)

lazy val frontend = project
  .in(file("frontend"))
  .settings(
    sharedSettings,
    Compile / npmDependencies ++= Seq(
      "react" -> "16.13.1",
      "react-dom" -> "16.13.1",
      "react-proxy" -> "1.1.8"
    ),
    Compile / npmDevDependencies ++= Seq(
      "file-loader" -> "6.0.0",
      "style-loader" -> "1.2.1",
      "css-loader" -> "3.5.3",
      "html-webpack-plugin" -> "4.3.0",
      "copy-webpack-plugin" -> "5.1.1",
      "webpack-merge" -> "4.2.2",
      "postcss-loader" -> "4.1.0",
      "postcss" -> "8.1.10",
      "tailwindcss" -> "2.0.1",
      "autoprefixer" -> "10.0.2",
      "react-icons" -> "4.1.0"
    ),
    libraryDependencies ++= Seq(
      "me.shadaj" %%% "slinky-web" % "0.6.6",
      "me.shadaj" %%% "slinky-hot" % "0.6.6",
      "org.scala-js" %%% "scalajs-dom" % "1.0.0"
    ),
    webpack / version := "4.43.0",
    startWebpackDevServer / version := "3.11.0",
    webpackResources := baseDirectory.value / "webpack" * "*",
    fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-fastopt.config.js"),
    fastOptJS / webpackDevServerExtraArgs := Seq("--inline", "--hot"),
    fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(),
    fullOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-opt.config.js"),
    Test / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-core.config.js"),
    Test / requireJsDomEnv := true,
    build := {
      Def
        .sequential(
          scalafixAll.toTask(""),
          scalafmtAll,
          Test / test,
          Compile / fullOptJS,
          deploy
        )
        .value
    },
    deploy := {
      val fs = (Compile / fullOptJS / webpack).value
      val outDir = (backend / baseDirectory).value / "public"

      fs.foreach(f =>
        sbt.io.IO.copyFile(f.data, outDir / (f.data.name))
      )
    }
  )
  .enablePlugins(ScalaJSBundlerPlugin)
  .dependsOn(data.js)
