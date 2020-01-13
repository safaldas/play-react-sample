name := """demo-play-vue"""
organization := "com.neoito"

version := "1.0-SNAPSHOT"


lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtWeb, SbtVuefy)
  .settings(
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice
    ),
    Assets / VueKeys.vuefy / VueKeys.prodCommands := Set("stage"),
    Assets / VueKeys.vuefy / VueKeys.webpackBinary := {
      // Detect windows
      if (sys.props.getOrElse("os.name", "").toLowerCase.contains("win")) {
        (new File(".") / "node_modules" / ".bin" / "webpack.cmd").getAbsolutePath
      } else {
        (new File(".") / "node_modules" / ".bin" / "webpack").getAbsolutePath
      }
    },
    Assets / VueKeys.vuefy / VueKeys.webpackConfig := (new File(".") / "webpack.config.js").getAbsolutePath,
    // All non-entry-points components, which are not included directly in HTML, should have the prefix `_`.
    // Webpack shouldn't compile non-entry-components directly. It's wasteful.
    Assets / VueKeys.vuefy / excludeFilter := "_*"
  )

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.neoito.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.neoito.binders._"
