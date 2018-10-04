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

package controllers.vat

import javax.inject.Inject

import config.FrontendAppConfig
import controllers.actions.{AuthAction, ServiceInfoAction}
import forms.vat.WhatIsYourVATRegNumberFormProvider
import identifiers.WhatIsYourVATRegNumberId
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Call
import services.VatSubscriptionService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.vat.whatIsYourVATRegNumber

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WhatIsYourVATRegNumberController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  navigator: Navigator[Call],
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  vatSubscriptionService: VatSubscriptionService,
  formProvider: WhatIsYourVATRegNumberFormProvider)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  private def getMandationStatus(vrn: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    vatSubscriptionService.getMandationStatus(vrn).map {
      _.getOrElse {
        Logger.error("There was an error retrieving mandation status")
        throw new Exception("There was an error retrieving mandation status")
      }
    }

  def onPageLoad() = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(whatIsYourVATRegNumber(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(whatIsYourVATRegNumber(appConfig, formWithErrors)(request.serviceInfoContent))),
        (value) =>
          getMandationStatus(value).map { mandationStatus =>
            Redirect(navigator.nextPage(WhatIsYourVATRegNumberId, (mandationStatus, value)))
        }
      )
  }
}
