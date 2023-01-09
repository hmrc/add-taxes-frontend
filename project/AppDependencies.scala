import sbt._

private object AppDependencies {

  import play.core.PlayVersion
  import play.sbt.PlayImport._
  
  val bootstrapVersion = "7.12.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc"                   %% "bootstrap-frontend-play-28"     % bootstrapVersion,
    "uk.gov.hmrc.mongo"             %% "hmrc-mongo-play-28"             % "0.74.0",
    "uk.gov.hmrc"                   %% "http-caching-client"            % "10.0.0-play-28",
    "uk.gov.hmrc"                   %% "play-frontend-hmrc"             % "5.5.0-play-28",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"           % "2.14.1"
  )

  abstract class TestDependencies(scope: String) {
    lazy val test: Seq[ModuleID] = Seq(
      "org.scalatestplus.play"     %% "scalatestplus-play"     % "5.1.0"             % scope,
      "com.typesafe.play"          %% "play-test"              % PlayVersion.current % scope,
      "org.pegdown"                % "pegdown"                 % "1.6.0"             % scope,
      "org.jsoup"                  % "jsoup"                   % "1.15.3"            % scope,
      "uk.gov.hmrc"                %% "bootstrap-test-play-28" % bootstrapVersion    % scope,
      "org.scalacheck"             %% "scalacheck"             % "1.17.0"            % scope,
      "org.scalatestplus"          %% "mockito-3-12"           % "3.2.10.0"          % scope,
      "com.github.tomakehurst"     % "wiremock-jre8"           % "2.35.0"            % scope,
      "org.mockito"                % "mockito-core"            % "4.11.0"            % scope,
//      "org.scoverage"                % "sbt-scoverage_2.12_1.0"            % "2.0.6"            % scope
    )
  }

  object Test extends TestDependencies("test")

  object ItTest extends TestDependencies("it")

  def apply(): Seq[ModuleID] = compile ++ Test.test ++ ItTest.test
}
