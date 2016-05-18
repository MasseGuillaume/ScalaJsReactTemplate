import Helper._

val commonSettings = Seq(
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xfuture",
    "-Xlint",
    "-Yinline-warnings",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused-import",
    "-Ywarn-value-discard"
  ),
  scalacOptions in (Compile, console) -= "-Ywarn-unused-import",
  organization := "org.example",
  version      := "0.1.0"
)

lazy val webapp = crossProject
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalatags" % "0.5.2",
      "com.lihaoyi" %%% "upickle"   % "0.3.8",
      "com.lihaoyi" %%% "autowire"  % "0.2.5"
    )
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka"                  %% "akka-http-experimental" % "2.4.4",
      "com.softwaremill.akka-http-session" %% "core"                   % "0.2.6"
    )
  )
  
lazy val webappJS = webapp.js
  .settings(
    libraryDependencies ++= {
      val scalajsReactVersion = "0.11.1"
      val scalaCssVersion = "0.4.1"
      Seq(
        "com.github.japgolly.scalacss"      %%% "core"      % scalaCssVersion,
        "com.github.japgolly.scalacss"      %%% "ext-react" % scalaCssVersion,
        "com.github.japgolly.scalajs-react" %%% "core"      % scalajsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "extra"     % scalajsReactVersion
      )
    },
    jsDependencies ++= {
      val react = "org.webjars.bower" % "react" % "15.0.1"
      Seq(
        react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
        react / "react-dom.js"         minified "react-dom.min.js"         commonJSName "ReactDOM"       dependsOn "react-with-addons.js",
        react / "react-dom-server.js"  minified "react-dom-server.min.js"  commonJSName "ReactDOMServer" dependsOn "react-dom.js"
      )
    }
  )

lazy val webappJVM = webapp.jvm
  .settings(packageScalaJs(webappJS))
