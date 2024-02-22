import sbt._

private object AppDependencies {

  import play.core.PlayVersion
  import play.sbt.PlayImport.*
  
  val bootstrapVersion = "8.4.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc"                   %% "bootstrap-frontend-play-30"     % bootstrapVersion,
    "uk.gov.hmrc.mongo"             %% "hmrc-mongo-play-30"             % "1.7.0",
    "uk.gov.hmrc"                   %% "http-caching-client-play-30"    % "11.2.0",
    "uk.gov.hmrc"                   %% "play-frontend-hmrc-play-30"     % "8.5.0",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"           % "2.16.1"
  )

  abstract class TestDependencies(scope: String) {
    lazy val test: Seq[ModuleID] = Seq(
      "org.scalatestplus.play"     %% "scalatestplus-play"     % "7.0.1"             % scope,
      "org.playframework"          %% "play-test"              % PlayVersion.current % scope,
      "org.pegdown"                % "pegdown"                 % "1.6.0"             % scope,
      "org.jsoup"                  % "jsoup"                   % "1.17.2"            % scope,
      "uk.gov.hmrc"                %% "bootstrap-test-play-30" % bootstrapVersion    % scope,
      "org.scalacheck"             %% "scalacheck"             % "1.17.0"            % scope,
      "org.scalatestplus"          %% "mockito-3-12"           % "3.2.10.0"          % scope,
      "org.wiremock"               % "wiremock"                % "3.4.0"             % scope,
      "org.mockito"                % "mockito-core"            % "5.10.0"            % scope,
    )
  }

  object Test extends TestDependencies("test")

  object ItTest extends TestDependencies("it")

  def apply(): Seq[ModuleID] = compile ++ Test.test ++ ItTest.test
}
