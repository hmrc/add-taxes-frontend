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

package controllers.deenrolment

import javax.inject.Inject
import config.FrontendAppConfig
import controllers.actions._
import controllers.deenrolment.routes._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Enrolments
import utils.Enrolments._

class DeenrolmentProxyController @Inject()(appConfig: FrontendAppConfig,
                                           mcc: MessagesControllerComponents,
                                           authenticate: AuthAction,
                                           serviceInfo: ServiceInfoAction)
  extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(service: Enrolments): Action[AnyContent] = (authenticate andThen serviceInfo) {
    val enrolmentRoutes: Map[Enrolments, Call] = Map(
      VATMOSS           -> DoYouNeedToLeaveVATMOSSController.onPageLoad(),
      VATMOSSNonUnion   -> DoYouNeedToStopVatMossNUController.onPageLoad(),
      SA                -> HaveYouStoppedSelfEmploymentController.onPageLoad(),
      RebatedOils       -> DoYouNeedToStopROController.onPageLoad(),
      EPAYE             -> DoYouNeedToStopEPAYEController.onPageLoad(),
      AddCis            -> DoYouWantToLeaveCISController.onPageLoad(),
      PSA               -> DoYouNeedToStopPSAController.onPageLoad(),
      CT                -> StopCorporationTaxController.onPageLoad(),
      MTDVAT            -> DoYouNeedToCancelMTDVATController.onPageLoad(),
      VAT               -> DoYouNeedToCancelVATController.onPageLoad(),
      GeneralBetting    -> DoYouNeedToStopGBDController.onPageLoad(),
      Charities         -> DoYouNeedToCloseCharityController.onPageLoad(),
      RemoteGaming      -> DoYouNeedToStopRGDController.onPageLoad(),
      PoolBetting       -> DoYouNeedToStopPBDController.onPageLoad(),
      MachineGamingDuty -> DoYouNeedToStopMGDController.onPageLoad(),
      ATWD              -> Call("GET", appConfig.atwdDeenrolmentUrl)
    )

    enrolmentRoutes.get(service) match {
      case Some(call) => Redirect(call)
      case None       => Redirect(appConfig.emacDeenrolmentsUrl(service))
    }
  }
}
