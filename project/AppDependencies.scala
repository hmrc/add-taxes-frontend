import sbt.*

private object AppDependencies {

  import play.core.PlayVersion
  import play.sbt.PlayImport.*
  
  val bootstrapVersion = "8.6.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"                   %% "bootstrap-frontend-play-30"     % bootstrapVersion,
    "uk.gov.hmrc.mongo"             %% "hmrc-mongo-play-30"             % "2.6.0",
    "uk.gov.hmrc"                   %% "http-caching-client-play-30"    % "11.2.0",
    "uk.gov.hmrc"                   %% "play-frontend-hmrc-play-30"     % "12.20.0",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"           % "2.16.1",
    "uk.gov.hmrc"                   %% "play-conditional-form-mapping-play-30" % "3.3.0"
  )

  abstract class TestDependencies(scope: String) {
    lazy val test: Seq[ModuleID] = Seq(
      "org.scalatestplus.play"     %% "scalatestplus-play"     % "7.0.1"             % scope,
      "org.playframework"          %% "play-test"              % PlayVersion.current % scope,
      "com.vladsch.flexmark"       %  "flexmark-all"           % "0.64.8"              % scope,
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
