/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers.vat.eurefunds

import javax.inject.Inject

import controllers.actions._
import config.FrontendAppConfig
import forms.vat.RegisteredForVATFormProvider
import identifiers.RegisteredForVATEURefundsId
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Call
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.Navigator
import viewmodels.ViewAction
import views.html.vat.registeredForVAT

import scala.concurrent.Future

class RegisteredForVATEURefundsController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  authenticate: AuthAction,
  navigator: Navigator[Call],
  serviceInfo: ServiceInfoAction,
  formProvider: RegisteredForVATFormProvider)
    extends FrontendController
    with I18nSupport {

  val form = formProvider()
  lazy val viewAction = ViewAction(routes.RegisteredForVATEURefundsController.onSubmit(), "VatEuRefundsNoVat")

  def onPageLoad = (authenticate andThen serviceInfo) { implicit request =>
    Ok(registeredForVAT(appConfig, form, viewAction)(request.serviceInfoContent))
  }

  def onSubmit = (authenticate andThen serviceInfo).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          Future.successful(
            BadRequest(registeredForVAT(appConfig, formWithErrors, viewAction)(request.serviceInfoContent))),
        (value) => Future.successful(Redirect(navigator.nextPage(RegisteredForVATEURefundsId, value)))
      )
  }
}
