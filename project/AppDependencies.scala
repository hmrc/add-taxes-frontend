import sbt._

private object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-frontend-play-27"    % "2.21.0",
    "uk.gov.hmrc" %% "simple-reactivemongo"          % "7.30.0-play-27",
    "uk.gov.hmrc" %% "logback-json-logger"           % "4.8.0",
    "uk.gov.hmrc" %% "govuk-template"                % "5.55.0-play-27",
    "uk.gov.hmrc" %% "play-health"                   % "3.15.0-play-27",
    "uk.gov.hmrc" %% "play-ui"                       % "8.12.0-play-27",
    "uk.gov.hmrc" %% "http-caching-client"           % "9.1.0-play-27",
    "uk.gov.hmrc" %% "play-language"                 % "4.3.0-play-27",
    "uk.gov.hmrc" %% "play-partials"                 % "6.11.0-play-27"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = Seq()
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest"          %% "scalatest"          % "3.0.4" % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3" % scope,
        "org.pegdown"            %  "pegdown"            % "1.6.0" % scope,
        "org.jsoup"              %  "jsoup"              % "1.10.3" % scope,
        "com.typesafe.play"      %% "play-test"          % PlayVersion.current % scope,
        "org.mockito"            %  "mockito-core"       % "3.3.3" % scope,
        "org.scalacheck"         %% "scalacheck"         % "1.14.0" % scope
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
