import sbt._

private object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28"    % "5.16.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"      % "0.50.0",
    "uk.gov.hmrc" %% "http-caching-client"           % "9.3.0-play-28",
    "uk.gov.hmrc" %% "play-frontend-hmrc" % "1.22.0-play-28"
  )

  abstract class TestDependencies(scope: String) {
    lazy val test : Seq[ModuleID] = Seq(
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % scope,
      "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
      "org.pegdown" % "pegdown" % "1.6.0" % scope,
      "org.jsoup" % "jsoup" % "1.13.1" % scope,
      "uk.gov.hmrc" %% "bootstrap-test-play-28" % "5.14.0"  % scope,
      "org.scalacheck"          %% "scalacheck"         % "1.15.3" % scope,
      "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % scope,
      "com.github.tomakehurst"  % "wiremock-jre8"        % "2.27.2" % scope,
      "org.mockito" % "mockito-core" % "3.7.7" % scope
      )
    }

  object Test extends TestDependencies("test")
  object ItTest extends TestDependencies("it")

  def apply(): Seq[ModuleID] = compile ++ Test.test ++ ItTest.test
}
