/*
 * Copyright 2022 HM Revenue & Customs
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
import config.featureToggles.FeatureSwitch.PptSwitch
import config.featureToggles.FeatureToggleSupport.isEnabled
import controllers.actions._
import forms.OtherTaxesFormProvider
import identifiers.OtherTaxesId
import models.OtherTaxes
import models.OtherTaxes._
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments => CoreEnrolments}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enrolments, Enumerable, Navigator, RadioOption}
import views.html.{organisation_only, otherTaxes}

import javax.inject.Inject
import scala.concurrent.Future

class OtherTaxesController @Inject()(mcc: MessagesControllerComponents,
                                     navigator: Navigator[Call],
                                     authenticate: AuthAction,
                                     serviceInfoData: ServiceInfoAction,
                                     formProvider: OtherTaxesFormProvider,
                                     otherTaxes: otherTaxes,
                                     organisation_only: organisation_only,
                                     implicit val appConfig: FrontendAppConfig)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[OtherTaxes] = formProvider()

  private[controllers] def getOptions(implicit r: ServiceInfoRequest[AnyContent]): Seq[RadioOption] = {
    val checks: Seq[CoreEnrolments => Option[RadioOption]] = Seq(
      checkAlcohol,
      checkAutomaticExchangeOfInformation,
      checkCharities,
      checkGamblingAndGaming,
      checkOilAndFuel,
      checkFulfilmentHouse,
      checkChildTrustFund,
      checkPODS,
      checkPPT
    )
    val defaultRadioOptions: Seq[RadioOption] = Seq(HousingAndLand, ImportsExports).map(_.toRadioOption)
    val unsortedRadioOptions: Seq[RadioOption] = checks.flatMap(_.apply(r.request.enrolments)) ++ defaultRadioOptions

    unsortedRadioOptions.sortBy(_.value)
  }

  private val checkAlcohol: CoreEnrolments => Option[RadioOption] =
    (_: CoreEnrolments) => Some(AlcoholAndTobacco.toRadioOption)

  private val checkAutomaticExchangeOfInformation: CoreEnrolments => Option[RadioOption] =
    _.getEnrolment(Enrolments.AEOI.toString)
      .fold[Option[RadioOption]](Some(AutomaticExchangeOfInformation.toRadioOption))(_ => None)

  private val checkCharities: CoreEnrolments => Option[RadioOption] =
    _.getEnrolment(Enrolments.Charities.toString)
      .fold[Option[RadioOption]](Some(Charities.toRadioOption))(_ => None)

  private val checkGamblingAndGaming: CoreEnrolments => Option[RadioOption] = { (enrolments: CoreEnrolments) =>
    val checks = List(
      checkMachineGamingDuty,
      checkGeneralBetting,
      checkRemoteGaming,
      checkPoolBetting
    )

    if (checks.forall(_.apply(enrolments))) None else Some(GamblingAndGaming.toRadioOption)
  }

  private val checkMachineGamingDuty: CoreEnrolments => Boolean =
    _.getEnrolment(Enrolments.MachineGamingDuty.toString).isDefined

  private val checkGeneralBetting: CoreEnrolments => Boolean =
    _.getEnrolment(Enrolments.GeneralBetting.toString).isDefined

  private val checkRemoteGaming: CoreEnrolments => Boolean =
    _.getEnrolment(Enrolments.RemoteGaming.toString).isDefined

  private val checkPoolBetting: CoreEnrolments => Boolean =
    _.getEnrolment(Enrolments.PoolBetting.toString).isDefined

  private val checkOilAndFuel: CoreEnrolments => Option[RadioOption] =
    (enrolments: CoreEnrolments) =>
      if (checkRebatedOils(enrolments) && checkTiedOils(enrolments)) {
        None
      } else {
        Some(OilAndFuel.toRadioOption)
    }

  private val checkRebatedOils: CoreEnrolments => Boolean =
    _.getEnrolment(Enrolments.RebatedOils.toString).isDefined

  private val checkTiedOils: CoreEnrolments => Boolean =
    _.getEnrolment(Enrolments.TiedOils.toString).isDefined

  private val checkFulfilmentHouse: CoreEnrolments => Option[RadioOption] =
    _.getEnrolment(Enrolments.OtherBusinessTaxDutyScheme.toString)
      .flatMap(_.getIdentifier(Enrolments.OtherBusinessTaxDutyScheme.FulfilmentHouseDueDiligenceSchemeIdentifier))
      .fold(Option(FulfilmentHouseDueDiligenceSchemeIntegration.toRadioOption))(_ => None)

  private val checkChildTrustFund: CoreEnrolments => Option[RadioOption] =
    _.getEnrolment(Enrolments.CTF.toString)
      .fold[Option[RadioOption]](Some(ChildTrustFund.toRadioOption))(_ => None)

  private val checkPODS: CoreEnrolments => Option[RadioOption] = e => {
    val pod = e.getEnrolment(Enrolments.PODSORG.toString)
    val podpp = e.getEnrolment(Enrolments.PODSPP.toString)
    if(pod.isDefined && podpp.isDefined) None else Some(PODS.toRadioOption)
  }

  private val checkPPT: CoreEnrolments => Option[RadioOption] = e => {
    val ppt: Option[Enrolment] = e.getEnrolment(Enrolments.PPT.toString)
    if(isEnabled(PptSwitch) && ppt.isEmpty) Some(PPT.toRadioOption) else None
  }

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    request.request.affinityGroup match {
      case Some(Organisation) => Ok(otherTaxes(appConfig, form, getOptions)(request.serviceInfoContent))
      case _                  => Ok(organisation_only(appConfig)(request.serviceInfoContent))
    }
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(otherTaxes(appConfig, formWithErrors, getOptions)(request.serviceInfoContent))),
        value => Future.successful(Redirect(navigator.nextPage(OtherTaxesId, value)))
      )
  }
}
