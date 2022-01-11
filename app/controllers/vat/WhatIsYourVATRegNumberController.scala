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

package controllers.vat

import config.FrontendAppConfig
import connectors.VatSubscriptionConnector
import controllers.actions.{AuthAction, ServiceInfoAction}
import forms.vat.WhatIsYourVATRegNumberFormProvider
import handlers.ErrorHandler
import identifiers.WhatIsYourVATRegNumberId
import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.vat.{vatAccountUnavailable, vatRegistrationException, whatIsYourVATRegNumber}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WhatIsYourVATRegNumberController @Inject()(appConfig: FrontendAppConfig,
                                                 mcc: MessagesControllerComponents,
                                                 navigator: Navigator[Call],
                                                 authenticate: AuthAction,
                                                 serviceInfoData: ServiceInfoAction,
                                                 vatSubscriptionConnector: VatSubscriptionConnector,
                                                 formProvider: WhatIsYourVATRegNumberFormProvider,
                                                 vatRegistrationException: vatRegistrationException,
                                                 vatAccountUnavailable: vatAccountUnavailable,
                                                 errorHandler: ErrorHandler,
                                                 whatIsYourVATRegNumber: whatIsYourVATRegNumber)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[String] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(whatIsYourVATRegNumber(appConfig, form)(request.serviceInfoContent))
  }

  def onPageLoadVatUnanavailable(): Action[AnyContent] = authenticate { implicit request =>
    Ok(vatAccountUnavailable(appConfig))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(whatIsYourVATRegNumber(appConfig, formWithErrors)(request.serviceInfoContent))),
        vrn =>
          vatSubscriptionConnector.getMandationStatus(vrn).map {
            case status if(status == OK || status == NOT_FOUND)  => Redirect(navigator.nextPage(WhatIsYourVATRegNumberId, (status, vrn)))
            case PRECONDITION_FAILED => Redirect(routes.WhatIsYourVATRegNumberController.onPageLoadVatUnanavailable())
            case _ => InternalServerError(errorHandler.internalServerErrorTemplate)
          }
      )
  }
}
