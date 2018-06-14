/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.i18n.Lang
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.config.ServicesConfig
import utils.{Enrolments, ForgottenOptions, PortalUrlBuilder}
import play.api.mvc.Request

@Singleton
class FrontendAppConfig @Inject()(override val runModeConfiguration: Configuration, environment: Environment)
    extends ServicesConfig
    with PortalUrlBuilder {

  override protected def mode = environment.mode

  private def loadConfig(key: String) =
    runModeConfiguration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  private lazy val contactHost = runModeConfiguration.getString("contact-frontend.host").getOrElse("")
  private val contactFormServiceIdentifier = "addtaxesfrontend"

  lazy val enrolmentManagementFrontendHost =
    runModeConfiguration.getString("enrolment-management-frontend.host").getOrElse("")
  lazy val governmentGatewayLostCredentialsFrontendHost =
    runModeConfiguration.getString("government-gateway-lost-credentials-frontend.host").getOrElse("")
  lazy val fulfilmentHouseHost = runModeConfiguration.getString("urls.fulfilment-house.host").getOrElse("")
  lazy val fulfilmentHouse = fulfilmentHouseHost + loadConfig("urls.fulfilment-house.schemeIntegration")

  lazy val analyticsToken = loadConfig(s"google-analytics.token")
  lazy val analyticsHost = loadConfig(s"google-analytics.host")
  lazy val ssoUrl = portalHost + loadConfig(s"urls.external.portal.sso")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"
  lazy val chiefOperationsEmail = loadConfig("chief.operations.email")

  lazy val authUrl = baseUrl("auth")
  lazy val btaUrl = baseUrl("business-tax-account")

  private lazy val businessAccountHost = runModeConfiguration.getString("urls.business-account.host").getOrElse("")
  def getBusinessAccountUrl(key: String): String = businessAccountHost + loadConfig(s"urls.business-account.$key")
  def getIFormUrl(key: String): String = loadConfig(s"urls.iForms-url.$key")

  private lazy val govUKHost = runModeConfiguration.getString("urls.govuk.host").getOrElse("")
  def getGovUKUrl(key: String): String = govUKHost + loadConfig(s"urls.govuk.$key")

  private lazy val govIMHost = runModeConfiguration.getString("urls.govim.host").getOrElse("")
  def getGovIMUrl(key: String): String = govIMHost + loadConfig(s"urls.govim.$key")

  lazy val loginUrl = loadConfig("urls.login")
  lazy val loginContinueUrl = loadConfig("urls.loginContinue")

  lazy val languageTranslationEnabled =
    runModeConfiguration.getBoolean("microservice.services.features.welsh-translation").getOrElse(true)

  def languageMap: Map[String, Lang] = Map("english" -> Lang("en"), "cymraeg" -> Lang("cy"))

  def routeToSwitchLanguage = (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  private lazy val portalHost = loadConfig(s"urls.external.portal.host")
  def getPortalUrl(key: String)(implicit request: Request[_]): String =
    appendLanguage(portalHost + loadConfig(s"urls.external.portal.$key"))

  lazy val hmceHost = loadConfig(s"urls.external.hmce.host")
  def getHmceURL(key: String): String = hmceHost + loadConfig(s"urls.external.hmce.$key")

  private lazy val onlineHost = runModeConfiguration.getString("urls.external.online.host").getOrElse("")
  def getOnlineUrl(key: String): String = onlineHost + loadConfig(s"urls.external.online.$key")

  lazy val customsHost = loadConfig("urls.external.customs.host")
  def getCustomsUrl(key: String): String = customsHost + loadConfig(s"urls.external.customs.$key")

  lazy val publishedAssets = loadConfig(s"urls.external.assets.host")
  def getPublishedAssetsUrl(key: String): String = publishedAssets + loadConfig(s"urls.external.assets.$key")

  def eiUrl = loadConfig(s"urls.external.ei")

  def emacEnrollmentsUrl(enrolment: Enrolments): String =
    s"$enrolmentManagementFrontendHost/enrolment-management-frontend/$enrolment/request-access-tax-scheme?continue=%2Fbusiness-account"

  def governmentGatewayLostCredentialsUrl(forgottenOption: ForgottenOptions): String =
    s"$governmentGatewayLostCredentialsFrontendHost/government-gateway-lost-credentials-frontend/" +
      s"choose-your-account?continue=%2Fbusiness-account&origin=business-tax-account&forgottenOption=$forgottenOption"
}
