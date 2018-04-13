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

package controllers

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import forms.OtherTaxesFormProvider
import identifiers.OtherTaxesId
import models.OtherTaxes.{AlcoholAndTobacco, AutomaticExchangeOfInformation, Charities, FulfilmentHouseDueDiligenceSchemeIntegration, GamblingAndGaming, HousingAndLand, ImportsExports, OilAndFuel}
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.AnyContent
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enrolments, Enumerable, Navigator, RadioOption}
import views.html.{organisation_only, otherTaxes}

import scala.concurrent.Future

class OtherTaxesController @Inject()(
                                      appConfig: FrontendAppConfig,
                                      override val messagesApi: MessagesApi,
                                      dataCacheConnector: DataCacheConnector,
                                      navigator: Navigator,
                                      authenticate: AuthAction,
                                      serviceInfoData: ServiceInfoAction,
                                      formProvider: OtherTaxesFormProvider) extends FrontendController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def getOptions(implicit r: ServiceInfoRequest[AnyContent]): Seq[RadioOption] = {
    val checks = Seq(checkAlcohol, checkAutomaticExchangeOfInformation, checkCharities, checkGamblingAndGaming,
      checkOilAndFuel, checkFulfilmentHouse)
    checks.flatMap(_.apply(r.request.enrolments)) ++ Some(HousingAndLand.toRadioOption) ++ Some(ImportsExports.toRadioOption)
  }

  private def checkAlcohol: (uk.gov.hmrc.auth.core.Enrolments) => Option[RadioOption] =
    (enrolments: uk.gov.hmrc.auth.core.Enrolments) =>
      if (checkAlcoholWholesalerRegistrationScheme(enrolments) && checkAlcoholAndTobaccoWarehousingDeclarations(enrolments)) {
        None
      } else {
        Some(AlcoholAndTobacco.toRadioOption)
      }

  private def checkAlcoholWholesalerRegistrationScheme: (uk.gov.hmrc.auth.core.Enrolments) => Boolean =
    _.getEnrolment(Enrolments.AlcoholWholesalerRegistrationScheme.toString).isDefined

  private def checkAlcoholAndTobaccoWarehousingDeclarations: (uk.gov.hmrc.auth.core.Enrolments) => Boolean =
    _.getEnrolment(Enrolments.AlcoholAndTobaccoWarehousingDeclarations.toString).isDefined

  private def checkAutomaticExchangeOfInformation: (uk.gov.hmrc.auth.core.Enrolments) => Option[RadioOption] =
    _.getEnrolment(Enrolments.AutomaticExchangeOfInformation.toString)
      .fold[Option[RadioOption]](Some(AutomaticExchangeOfInformation.toRadioOption))(_ => None)

  private def checkCharities: (uk.gov.hmrc.auth.core.Enrolments) => Option[RadioOption] =
    _.getEnrolment(Enrolments.Charities.toString)
      .fold[Option[RadioOption]](Some(Charities.toRadioOption))(_ => None)

  private def checkGamblingAndGaming: (uk.gov.hmrc.auth.core.Enrolments) => Option[RadioOption] = {
    (enrolments: uk.gov.hmrc.auth.core.Enrolments) =>

      val checks = List(
        checkMachineGamingDuty,
        checkGeneralBetting,
        checkRemoteGaming,
        checkPoolBetting
      )

      if (checks.map(_.apply(enrolments)).forall(_ == true)) None
      else Some(GamblingAndGaming.toRadioOption)
  }

  private def checkMachineGamingDuty: (uk.gov.hmrc.auth.core.Enrolments) => Boolean =
    _.getEnrolment(Enrolments.MachineGamingDuty.toString).isDefined

  private def checkGeneralBetting: (uk.gov.hmrc.auth.core.Enrolments) => Boolean =
    _.getEnrolment(Enrolments.GeneralBetting.toString).isDefined

  private def checkRemoteGaming: (uk.gov.hmrc.auth.core.Enrolments) => Boolean =
    _.getEnrolment(Enrolments.RemoteGaming.toString).isDefined

  private def checkPoolBetting: (uk.gov.hmrc.auth.core.Enrolments) => Boolean =
    _.getEnrolment(Enrolments.PoolBetting.toString).isDefined

  private def checkOilAndFuel: (uk.gov.hmrc.auth.core.Enrolments) => Option[RadioOption] =
    (enrolments: uk.gov.hmrc.auth.core.Enrolments) =>
      if (checkRebatedOils(enrolments) && checkTiedOils(enrolments)) {
        None
      } else {
        Some(OilAndFuel.toRadioOption)
      }

  private def checkRebatedOils: (uk.gov.hmrc.auth.core.Enrolments) => Boolean =
    _.getEnrolment(Enrolments.RebatedOils.toString).isDefined

  private def checkTiedOils: (uk.gov.hmrc.auth.core.Enrolments) => Boolean =
    _.getEnrolment(Enrolments.TiedOils.toString).isDefined


  private def checkFulfilmentHouse: (uk.gov.hmrc.auth.core.Enrolments) => Option[RadioOption] =
    _.getEnrolment(Enrolments.FulfilmentHouseDueDiligenceSchemeIntegration.toString)
      .fold[Option[RadioOption]](Some(FulfilmentHouseDueDiligenceSchemeIntegration.toRadioOption))(_ => None)

  def onPageLoad() = (authenticate andThen serviceInfoData) {
    implicit request =>
      request.request.affinityGroup match {
        case Some(Individual) => Ok(organisation_only(appConfig)(request.serviceInfoContent))
        case _ => Ok(otherTaxes(appConfig, form, getOptions)(request.serviceInfoContent))
      }
  }

  def onSubmit() = (authenticate andThen serviceInfoData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(otherTaxes(appConfig, formWithErrors, getOptions)(request.serviceInfoContent))),
        (value) =>
          Future.successful(Redirect(navigator.nextPage(OtherTaxesId, value)))
      )
  }
}
