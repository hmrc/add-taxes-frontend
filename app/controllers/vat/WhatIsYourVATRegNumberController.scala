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

package controllers.vat

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.BypassVATETMPCheck
import config.featureToggles.FeatureToggleSupport
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

import scala.concurrent.{ExecutionContext, Future}

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
                                                 whatIsYourVATRegNumber: whatIsYourVATRegNumber)(
                                                implicit val ec: ExecutionContext
  ) extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits with FeatureToggleSupport {

  def form: Form[String] = formProvider()

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
          if(isDisabled(BypassVATETMPCheck)(appConfig)) {
            vatSubscriptionConnector.getMandationStatus(vrn).map {
              case status if status == OK || status == NOT_FOUND =>
                infoLog(s"[WhatIsYourVATRegNumberController][onSubmit] mandation status: $status")
                Redirect(navigator.nextPage(WhatIsYourVATRegNumberId, (status, vrn)))
              case PRECONDITION_FAILED =>
                infoLog(s"[WhatIsYourVATRegNumberController][onSubmit] mandation status: $PRECONDITION_FAILED")
                Redirect(routes.WhatIsYourVATRegNumberController.onPageLoadVatUnanavailable())
              case status =>
                infoLog(s"[WhatIsYourVATRegNumberController][onSubmit] mandation status: $status")
                InternalServerError(errorHandler.internalServerErrorTemplate)
            }
          } else {
            infoLog(s"[WhatIsYourVATRegNumberController][onSubmit] DisableETMPCheck is enabled. No mandation check made")
            Future.successful(Redirect(navigator.nextPage(WhatIsYourVATRegNumberId, (OK, vrn))))
          }
      )
  }
}
