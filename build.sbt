
val Http4sVersion = "0.21.7" // --
val RhoCoreVersion = "0.21.0-RC1"
val RhoSwaggerVersion = "0.21.0-RC1"
val Specs2Version = "4.0.3"
val LogbackVersion = "1.2.3"
val CirceVersion = "0.13.0"
val SlickVersion = "3.2.3"
val SlickHickariCPVersion = "3.2.3"
val SlickPGVersion = "0.16.1"

lazy val root = (project in file("."))
  .settings(
    organization := "com.terefe",
    name         := "hello-http4s",
    version      := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.12",
    autoCompilerPlugins := true,
      libraryDependencies ++= Seq(
      "org.http4s"          %% "http4s-blaze-server"  % Http4sVersion,
      "org.http4s"          %% "http4s-circe"         % Http4sVersion,
      "org.http4s"          %% "http4s-dsl"           % Http4sVersion,
      "org.http4s"          %% "rho-core"             % RhoCoreVersion,
      "org.http4s"          %% "rho-swagger"          % RhoSwaggerVersion,

      "io.circe"            %% "circe-generic"        % CirceVersion,
      "io.circe"            %% "circe-literal"        % CirceVersion,
      "io.circe"            %% "circe-generic-extras" % CirceVersion,
      "io.circe"            %% "circe-optics"         % CirceVersion,
      "io.circe"            %% "circe-parser"         % CirceVersion,

      "org.specs2"          %% "specs2-core"          % Specs2Version % "test",
      "ch.qos.logback"      %  "logback-classic"      % LogbackVersion,

      "com.typesafe.slick"  %% "slick"                % SlickVersion,
      "com.typesafe.slick"  %% "slick-hikaricp"       % SlickHickariCPVersion,
      "com.github.tminglei" %% "slick-pg"             % SlickPGVersion
    )
  )




