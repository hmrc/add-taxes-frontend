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
import controllers.actions._
import forms.vat.RegisterForVATOnlineFormProvider
import identifiers.RegisterForVATOnlineId

import javax.inject.Inject
import models.vat.RegisterForVATOnline
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import service.ThresholdService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.vat.registerForVATOnline

class RegisterForVATOnlineController @Inject()(appConfig: FrontendAppConfig,
                                               mcc: MessagesControllerComponents,
                                               navigator: Navigator[Call],
                                               authenticate: AuthAction,
                                               serviceInfoData: ServiceInfoAction,
                                               formProvider: RegisterForVATOnlineFormProvider,
                                               registerForVATOnline: registerForVATOnline,
                                               thresholdService: ThresholdService)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[RegisterForVATOnline] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(registerForVATOnline(appConfig, form, thresholdService.formattedVatThreshold())(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => BadRequest(registerForVATOnline(appConfig, formWithErrors, thresholdService.formattedVatThreshold())(request.serviceInfoContent)),
        value => Redirect(navigator.nextPage(RegisterForVATOnlineId, value))
      )
  }
}
