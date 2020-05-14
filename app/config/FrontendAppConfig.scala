/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.Configuration
import utils.{Enrolments, ForgottenOptions, PortalUrlBuilder}
import play.api.mvc.{Call, Request}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.language.LanguageUtils

@Singleton
class FrontendAppConfig @Inject()(val config: ServicesConfig,
                                  val conf: Configuration,
                                  val languageUtils: LanguageUtils) extends PortalUrlBuilder with FeatureToggles {

  private lazy val contactHost = config.getString("contact-frontend.host")
  private val contactFormServiceIdentifier = "addtaxesfrontend"

  lazy val enrolmentManagementFrontendHost: String = config.getString("enrolment-management-frontend.host")
  lazy val lostCredentialsFrontendHost: String = config.getString("lost-credentials-frontend.host")
  lazy val fulfilmentHouseHost: String = config.getString("urls.fulfilment-house.host")
  lazy val fulfilmentHouse: String = fulfilmentHouseHost + config.getString("urls.fulfilment-house.schemeIntegration")

  lazy val podsHost: String = config.getString("urls.manage-pensions-frontend.host")
  lazy val pods: String = podsHost + config.getString("urls.manage-pensions-frontend.schemesOverview")

  lazy val analyticsToken: String = config.getString(s"google-analytics.token")
  lazy val analyticsHost: String = config.getString(s"google-analytics.host")
  lazy val ssoUrl: String = portalHost + config.getString(s"urls.external.portal.sso")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"
  lazy val chiefOperationsEmail: String = config.getString("chief.operations.email")

  lazy val authUrl: String = config.baseUrl("auth")

  lazy val vatSubscriptionUrl: String = config.baseUrl("vat-subscription")
  lazy val btaUrl: String = config.baseUrl("business-tax-account")

  private lazy val stampDutyEnrollmentHost = config.getString("stamp-duty-land-tax-enrolment-frontend.host")

  def getStampDutyUrl(key: String): String = stampDutyEnrollmentHost + config.getString(s"stamp-duty-land-tax-enrolment-frontend.$key")

  private lazy val businessAccountHost = config.getString("urls.business-account.host")

  def getBusinessAccountUrl(key: String): String = businessAccountHost + config.getString(s"urls.business-account.$key")

  def getIFormUrl(key: String): String = config.getString(s"urls.iForms-url.$key")

  private lazy val govUKHost = config.getString("urls.govuk.host")

  def getGovUKUrl(key: String): String = govUKHost + config.getString(s"urls.govuk.$key")

  private lazy val ggRegistrationHost = config.getString("government-gateway-registration-frontend.host")

  def getGGRegistrationUrl(key: String): String = ggRegistrationHost + config.getString(s"government-gateway-registration-frontend.$key")

  private lazy val govIMHost = config.getString("urls.govim.host")

  def getGovIMUrl(key: String): String = govIMHost + config.getString(s"urls.govim.$key")

  lazy val loginUrl: String = config.getString("urls.login")
  lazy val loginContinueUrl: String = config.getString("urls.loginContinue")

  lazy val languageTranslationEnabled: Boolean = config.getBoolean("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map("english" -> Lang("en"), "cymraeg" -> Lang("cy"))

  val routeToSwitchLanguage: String => Call = (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  private lazy val portalHost = config.getString(s"urls.external.portal.host")

  def getPortalUrl(key: String, args: String*)(implicit request: Request[_]): String = {
    appendLanguage(portalHost + config.getString(s"urls.external.portal.$key")).format(args: _*)
  }

  lazy val hmceHost: String = config.getString(s"urls.external.hmce.host")

  def getHmceURL(key: String): String = hmceHost + config.getString(s"urls.external.hmce.$key")

  lazy val customsHost: String = config.getString("urls.external.customs.host")

  def getCustomsUrl(key: String): String = customsHost + config.getString(s"urls.external.customs.$key")

  lazy val publishedAssets: String = config.getString(s"urls.external.assets.host")

  def getPublishedAssetsUrl(key: String): String = publishedAssets + config.getString(s"urls.external.assets.$key")

  lazy val eiUrl: String = config.getString("urls.external.ei")

  lazy val atedUrl: String = config.getString("urls.external.ated")

  lazy val revenueScotUrl: String = config.getString("urls.external.lbtScotland")

  lazy val vatSignUpHost: String = config.getString("vat-sign-up-frontend.host")

  def vatSignUpClaimSubscriptionUrl(vrn: String): String = s"$vatSignUpHost/vat-through-software/sign-up/claim-subscription/$vrn"

  def emacEnrollmentsUrl(enrolment: Enrolments): String = {
    s"$enrolmentManagementFrontendHost/enrolment-management-frontend/$enrolment/request-access-tax-scheme?continue=%2Fbusiness-account"
  }

  def emacDeenrolmentsUrl(enrolment: Enrolments): String = {
    s"$enrolmentManagementFrontendHost/enrolment-management-frontend/$enrolment/remove-access-tax-scheme?continue=%2Fbusiness-account"
  }

  lazy val atwdDeenrolmentUrl = s"$enrolmentManagementFrontendHost/enrolment-management-frontend/HMCE-ATWD-ORG/remove-warehouse?continue=/account"

  def lostCredentials(forgottenOption: ForgottenOptions): String = s"$lostCredentialsFrontendHost/account-recovery/choose-account-type/$forgottenOption"

  lazy val checkUtrHost: String = config.getString("enrolment-store-proxy.host")

  def checkUtrUrl(utr: String): String = s"$checkUtrHost/enrolment-store-proxy/enrolment-store/enrolments/IR-SA~UTR~$utr/users?type=principal"

  private lazy val pensionsHost: String = config.getString("urls.external.pensions.host")

  def getPensionsUrl(key: String): String = pensionsHost + config.getString(s"urls.external.pensions.$key")

  lazy val googleTagManagerId: String = config.getString(s"google-tag-manager.id")

  lazy val changeBusinessDetailsHost: String = config.getString("urls.manage-vat-subscription-frontend.host")
  lazy val changeBusinessDetailsUri: String = config.getString("urls.manage-vat-subscription-frontend.changeBusinessDetails")
  lazy val changeBusinessDetailsUrl: String = changeBusinessDetailsHost + changeBusinessDetailsUri
}

trait FeatureToggles {
  val config: ServicesConfig

  private def featureEnabled(key: String): Boolean = config.getBoolean(s"feature-toggles.$key")

  lazy val stampDutyEnabled: Boolean = featureEnabled("stampduty")
  lazy val employerPayeJourneyEnabled: Boolean = featureEnabled("employer.paye")
  lazy val mtdVatSignUpJourneyEnabled: Boolean = featureEnabled("mtd-vat-signup")
  lazy val newVatJourneyEnabled: Boolean = featureEnabled("newVatJourney")

  final val sessionTimeoutInSeconds: Long = 900
  final val sessionCountdownInSeconds: Int = 60
}
