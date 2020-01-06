/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.vat.moss.ukbased

import config.FrontendAppConfig
import controllers.actions._
import forms.vat.moss.AlreadyRegisteredForVATMossFormProvider
import identifiers.AlreadyRegisteredForVATMossId.UkBased
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Call
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.Navigator
import viewmodels.ViewAction
import views.html.vat.moss.alreadyRegisteredForVATMoss

class AlreadyRegisteredForVATMossController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  authenticate: AuthAction,
  navigator: Navigator[Call],
  serviceInfo: ServiceInfoAction,
  formProvider: AlreadyRegisteredForVATMossFormProvider)
    extends FrontendController
    with I18nSupport {

  val form = formProvider()
  lazy val viewAction =
    ViewAction(routes.AlreadyRegisteredForVATMossController.onSubmit(), "VatMossUkVatRegistered")

  def view(form: Form[_] = form)(implicit request: ServiceInfoRequest[_]): Html =
    alreadyRegisteredForVATMoss(appConfig, form, viewAction)(request.serviceInfoContent)

  def onPageLoad = (authenticate andThen serviceInfo) { implicit request =>
    Ok(view())
  }

  def onSubmit = (authenticate andThen serviceInfo) { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) => BadRequest(view(formWithErrors)),
        (value) => Redirect(navigator.nextPage(UkBased, value))
      )
  }
}
