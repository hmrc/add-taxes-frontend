/*
 * Copyright 2024 HM Revenue & Customs
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

import config.FrontendAppConfig
import config.featureToggles.FeatureToggleSupport.isEnabled
import controllers.actions._
import forms.OtherTaxesFormProvider
import identifiers.OtherTaxesId
import models.OtherTaxes
import models.OtherTaxes.{AlcoholAndTobacco, GamblingAndGaming, HousingAndLand, ImportsExports, OilAndFuel, PODS, PPT}
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{Enrolments => CoreEnrolments}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Enrolments._
import utils._
import views.html.{organisation_only, otherTaxes}

import javax.inject.Inject
import scala.concurrent.Future

class OtherTaxesController @Inject() (mcc: MessagesControllerComponents,
                                      navigator: Navigator[Call],
                                      authenticate: AuthAction,
                                      serviceInfoData: ServiceInfoAction,
                                      formProvider: OtherTaxesFormProvider,
                                      otherTaxes: otherTaxes,
                                      organisation_only: organisation_only,
                                      implicit val appConfig: FrontendAppConfig)
    extends FrontendController(mcc)
    with I18nSupport
    with Enumerable.Implicits {

  val form: Form[OtherTaxes] = formProvider()

  private[controllers] def getOptions(implicit r: ServiceInfoRequest[AnyContent]): Seq[RadioOption] = {
    val allUserEnrolments: uk.gov.hmrc.auth.core.Enrolments = r.request.enrolments

    val permanentRadios: Set[RadioOption] =
      Set(AlcoholAndTobacco, HousingAndLand, ImportsExports, PPT).map(_.toRadioOption)

    val singleEnrolmentChecks: Set[OtherTaxEnrolment] = Set(AEOI, Charities, CTF, ECL, OtherBusinessTaxDutyScheme, PLRID, VPD)
    val radiosFromSingleChecksWithSwitches: Set[RadioOption] =
      singleEnrolmentChecks.flatMap(returnFeatureSwitchedRadioIfSingleEnrolmentIsNotPresent(_, allUserEnrolments))

    val gamblingAndGamingSubMenuRadio: Option[RadioOption] =
      returnSubMenuRadioIfAllSubEnrolmentsAreNotPresent(Enrolments.allGamblingAndGaming, GamblingAndGaming.toRadioOption, allUserEnrolments)

    val oilAndFuelSubMenuRadio: Option[RadioOption] =
      returnSubMenuRadioIfAllSubEnrolmentsAreNotPresent(Enrolments.allOilAndFuel, OilAndFuel.toRadioOption, allUserEnrolments)

    val podsSubMenuRadio: Option[RadioOption] =
      returnSubMenuRadioIfAllSubEnrolmentsAreNotPresent(Enrolments.allPODS, PODS.toRadioOption, allUserEnrolments)

    val allRemainingRadios: Set[RadioOption] =
      permanentRadios ++ radiosFromSingleChecksWithSwitches ++ Set(gamblingAndGamingSubMenuRadio, oilAndFuelSubMenuRadio, podsSubMenuRadio).flatten

    allRemainingRadios.toSeq.sortBy(_.value)
  }

  private def returnFeatureSwitchedRadioIfSingleEnrolmentIsNotPresent(enrolmentToCheck: OtherTaxEnrolment,
                                                                      allUserEnrolments: CoreEnrolments): Option[RadioOption] = {
    val notYetEnrolled     = allUserEnrolments.getEnrolment(enrolmentToCheck.identifier).isEmpty
    val switchOnOrNoSwitch = enrolmentToCheck.featureSwitch.forall(isEnabled)

    if (notYetEnrolled && switchOnOrNoSwitch) Some(enrolmentToCheck.toOtherTaxRadioOption) else None
  }

  private def returnSubMenuRadioIfAllSubEnrolmentsAreNotPresent(enrolmentsToCheck: Set[_ <: Enrolments],
                                                                subMenuRadio: RadioOption,
                                                                allUserEnrolments: CoreEnrolments): Option[RadioOption] = {
    val userHasAllEnrolments = enrolmentsToCheck.forall(e => allUserEnrolments.getEnrolment(e.toString).isDefined)
    if (userHasAllEnrolments) None else Some(subMenuRadio)
  }

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    request.request.affinityGroup match {
      case Some(Organisation) => Ok(otherTaxes(appConfig, form, getOptions)(request.serviceInfoContent))
      case _                  => Ok(organisation_only(appConfig)(request.serviceInfoContent))
    }
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(otherTaxes(appConfig, formWithErrors, getOptions)(request.serviceInfoContent))),
        value => Future.successful(Redirect(navigator.nextPage(OtherTaxesId, value)))
      )
  }
}
