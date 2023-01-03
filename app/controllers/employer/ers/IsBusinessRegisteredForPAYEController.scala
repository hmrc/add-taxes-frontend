/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.employer.ers

import config.FrontendAppConfig
import controllers.actions._
import forms.employer.IsBusinessRegisteredForPAYEFormProvider
import identifiers.IsBusinessRegisteredForPAYEId
import javax.inject.Inject
import models.employer.IsBusinessRegisteredForPAYE
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Navigator
import viewmodels.ViewAction
import views.html.employer.isBusinessRegisteredForPAYE

class IsBusinessRegisteredForPAYEController @Inject()(appConfig: FrontendAppConfig,
                                                      mcc: MessagesControllerComponents,
                                                      authenticate: AuthAction,
                                                      navigator: Navigator[Call],
                                                      serviceInfo: ServiceInfoAction,
                                                      formProvider: IsBusinessRegisteredForPAYEFormProvider,
                                                      isBusinessRegisteredForPAYE: isBusinessRegisteredForPAYE)
  extends FrontendController(mcc) with I18nSupport {

  val form: Form[IsBusinessRegisteredForPAYE] = formProvider()
  lazy val viewAction: ViewAction = ViewAction(routes.IsBusinessRegisteredForPAYEController.onSubmit(), "AddErsEpayeRegistered")

  def onPageLoad: Action[AnyContent] = (authenticate andThen serviceInfo) { implicit request =>
    Ok(isBusinessRegisteredForPAYE(appConfig, form, viewAction)(request.serviceInfoContent))
  }

  def onSubmit: Action[AnyContent] = (authenticate andThen serviceInfo) { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => BadRequest(isBusinessRegisteredForPAYE(appConfig, formWithErrors, viewAction)(request.serviceInfoContent)),
        value => Redirect(navigator.nextPage(IsBusinessRegisteredForPAYEId.ERS, value))
      )
  }
}
