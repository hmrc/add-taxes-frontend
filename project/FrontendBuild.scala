import sbt._

object FrontendBuild extends Build with MicroService {

  val appName = "add-taxes-frontend"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  private val logbackJsonLoggerVersion = "4.6.0"
  private val scalaTestVersion = "3.0.4"
  private val playReactivemongoVersion = "6.8.0"
  private val scalacheckVersion = "1.14.0"
  private val referenceCheckerVersion = "2.4.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % "1.7.0",
    "uk.gov.hmrc" %% "play-reactivemongo" % playReactivemongoVersion,
    "uk.gov.hmrc" %% "logback-json-logger" % logbackJsonLoggerVersion,
    "uk.gov.hmrc" %% "govuk-template" % "5.54.0-play-26",
    "uk.gov.hmrc" %% "play-health" % "3.15.0-play-26",
    "uk.gov.hmrc" %% "play-ui" % "8.9.0-play-26",
    "uk.gov.hmrc" %% "http-caching-client" % "9.0.0-play-26",
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % "1.2.0-play-26",
    "uk.gov.hmrc" %% "play-language" % "4.2.0-play-26",
    "uk.gov.hmrc" %% "play-partials" % "6.10.0-play-26",
    "uk.gov.hmrc" %% "reference-checker" % referenceCheckerVersion
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = Seq()
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3" % scope,
        "org.pegdown" % "pegdown" % "1.6.0" % scope,
        "org.jsoup" % "jsoup" % "1.10.3" % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.mockito" % "mockito-core" % "3.3.3" % scope,
        "org.scalacheck" %% "scalacheck" % scalacheckVersion % scope
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
