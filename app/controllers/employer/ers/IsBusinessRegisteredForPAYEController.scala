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

package controllers.employer.ers

import javax.inject.Inject

import config.FrontendAppConfig
import controllers.actions._
import forms.employer.IsBusinessRegisteredForPAYEFormProvider
import identifiers.IsBusinessRegisteredForPAYEId
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.Navigator
import viewmodels.ViewAction
import views.html.employer.isBusinessRegisteredForPAYE

class IsBusinessRegisteredForPAYEController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  authenticate: AuthAction,
  navigator: Navigator,
  serviceInfo: ServiceInfoAction,
  formProvider: IsBusinessRegisteredForPAYEFormProvider)
    extends FrontendController
    with I18nSupport {

  val form = formProvider()
  lazy val viewAction = ViewAction(routes.IsBusinessRegisteredForPAYEController.onSubmit(), "AddErsEpayeRegistered")

  def onPageLoad = (authenticate andThen serviceInfo) { implicit request =>
    Ok(isBusinessRegisteredForPAYE(appConfig, form, viewAction)(request.serviceInfoContent))
  }

  def onSubmit = (authenticate andThen serviceInfo) { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          BadRequest(isBusinessRegisteredForPAYE(appConfig, formWithErrors, viewAction)(request.serviceInfoContent)),
        (value) => Redirect(navigator.nextPage(IsBusinessRegisteredForPAYEId.ERS, value))
      )
  }
}
