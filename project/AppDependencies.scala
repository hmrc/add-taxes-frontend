import sbt._

private object AppDependencies {

  import play.core.PlayVersion
  import play.sbt.PlayImport._

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % "5.20.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.59.0",
    "uk.gov.hmrc" %% "http-caching-client" % "9.5.0-play-28",
    "uk.gov.hmrc" %% "play-frontend-hmrc" % "3.4.0-play-28",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala" %  "2.13.1"
  )

  abstract class TestDependencies(scope: String) {
    lazy val test: Seq[ModuleID] = Seq(
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % scope,
      "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
      "org.pegdown" % "pegdown" % "1.6.0" % scope,
      "org.jsoup" % "jsoup" % "1.14.3" % scope,
      "uk.gov.hmrc" %% "bootstrap-test-play-28" % "5.20.0" % scope,
      "org.scalacheck" %% "scalacheck" % "1.15.4" % scope,
      "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % scope,
      "com.github.tomakehurst" % "wiremock-jre8" % "2.32.0" % scope,
      "org.mockito" % "mockito-core" % "4.3.1" % scope
    )
  }

  object Test extends TestDependencies("test")

  object ItTest extends TestDependencies("it")

  def apply(): Seq[ModuleID] = compile ++ Test.test ++ ItTest.test
}
