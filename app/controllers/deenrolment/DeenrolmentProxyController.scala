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

package controllers.deenrolment

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import controllers.actions._
import config.FrontendAppConfig
import utils.Enrolments
import controllers.deenrolment.routes._

class DeenrolmentProxyController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  authenticate: AuthAction,
  serviceInfo: ServiceInfoAction)
    extends FrontendController
    with I18nSupport {

  def onPageLoad(service: Enrolments) = (authenticate andThen serviceInfo) { implicit request =>
    service match {
      case Enrolments.SA             => Redirect(HaveYouStoppedSelfEmploymentController.onPageLoad())
      case Enrolments.RebatedOils    => Redirect(DoYouNeedToStopROController.onPageLoad())
      case Enrolments.EPAYE          => Redirect(DoYouNeedToStopEPAYEController.onPageLoad())
      case Enrolments.AddCis         => Redirect(DoYouWantToLeaveCISController.onPageLoad())
      case Enrolments.PSA            => Redirect(DoYouNeedToStopPSAController.onPageLoad())
      case Enrolments.CT             => Redirect(StopCorporationTaxController.onPageLoad())
      case Enrolments.VAT            => Redirect(DoYouNeedToCancelVATController.onPageLoad())
      case Enrolments.GeneralBetting => Redirect(DoYouNeedToStopGBDController.onPageLoad())
      case Enrolments.RemoteGaming   => Redirect(DoYouNeedToStopRGDController.onPageLoad())
      case _                         => Redirect(appConfig.emacDeenrolmentsUrl(service))
    }
  }
}
