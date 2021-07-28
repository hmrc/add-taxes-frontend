import com.typesafe.sbt.web.Import.WebKeys.assets
import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, defaultSettings, scalaSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import uk.gov.hmrc.sbtsettingkeys.Keys.isPublicArtefact


lazy val appName: String = "add-taxes-frontend"
lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq(
  RoutesKeys.routesImport ++= Seq(),
  TwirlKeys.templateImports ++= Seq()
)
val silencerVersion = "1.7.1"

def unitFilter(name: String): Boolean = name startsWith "unit"

def getSourceDirectories(root: File, testType: String) = Seq(root / s"test/$testType")
def getResourceDirectories(root: File, testType: String) = Seq(root / s"test/$testType", root / "target/web/public/test")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins : _*)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(playSettings : _*)
  .settings(RoutesKeys.routesImport ++= Seq(
    "models._",
    "config.Binders._",
    "playconfig.featuretoggle.Feature",
    "testonly.FeatureQueryBinder._"
  ))
  .settings(
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*models.*;.*identifiers.*;.*repositories.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*DataCacheConnector;" +
      ".*ControllerConfiguration;.*LanguageSwitchController;.*testonly.*",
    ScoverageKeys.coverageMinimum := 93.98,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in Test := false
  )
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    // ***************
    // Use the silencer plugin to suppress warnings from unused imports in compiled twirl templates
    scalacOptions += "-P:silencer:pathFilters=views;routes",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    ),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    PlayKeys.playDefaultPort := 9730,
    scalaVersion := "2.12.12",
    isPublicArtefact := true
  )
  .settings(inConfig(Test)(Defaults.testSettings): _*)
  .settings(
    addTestReportOption(Test, "test-reports"),
    unmanagedSourceDirectories in Test := (baseDirectory in Test)(base => getSourceDirectories(base, "unit")).value,
    unmanagedResourceDirectories in Test := (baseDirectory in Test)(base => getResourceDirectories(base, "unit")).value
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest) (base => getSourceDirectories(base, "it")).value,
    unmanagedResourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest) (base => getResourceDirectories(base, "it")).value,
    Keys.fork in IntegrationTest := true,
    parallelExecution in IntegrationTest:= false,
    addTestReportOption(IntegrationTest, "it-test-reports"),
    (compile in IntegrationTest) := (compile in IntegrationTest).dependsOn(assets in TestAssets).value,
    unmanagedClasspath in IntegrationTest += ((baseDirectory in IntegrationTest) map { base => Attributed.blank(base / "target/web/public/test") }).value
  )
  .settings(
    Keys.fork in Test := true,
    Keys.fork in IntegrationTest := false,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    parallelExecution in IntegrationTest := false
  )
  .settings(
    // concatenate js
    Concat.groups := Seq(
      "javascripts/addtaxesfrontend-app.js" -> group(Seq("javascripts/show-hide-content.js", "javascripts/addtaxesfrontend.js"))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    pipelineStages in Assets := Seq(concat,uglify),
    // only compress files generated by concat
    includeFilter in uglify := GlobFilter("addtaxesfrontend-*.js"))
  .settings(majorVersion := 0)
