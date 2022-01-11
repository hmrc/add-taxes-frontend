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

package controllers.vat.moss.ukbased

import config.FrontendAppConfig
import controllers.actions._
import forms.vat.moss.AlreadyRegisteredForVATMossFormProvider
import identifiers.AlreadyRegisteredForVATMossId.UkBased
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.vat.moss.AlreadyRegisteredForVATMoss
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Navigator
import viewmodels.ViewAction
import views.html.vat.moss.alreadyRegisteredForVATMoss

class AlreadyRegisteredForVATMossController @Inject()(appConfig: FrontendAppConfig,
                                                      mcc: MessagesControllerComponents,
                                                      authenticate: AuthAction,
                                                      navigator: Navigator[Call],
                                                      serviceInfo: ServiceInfoAction,
                                                      formProvider: AlreadyRegisteredForVATMossFormProvider,
                                                      alreadyRegisteredForVATMoss: alreadyRegisteredForVATMoss)
  extends FrontendController(mcc) with I18nSupport {

  val form: Form[AlreadyRegisteredForVATMoss] = formProvider()
  lazy val viewAction: ViewAction = ViewAction(routes.AlreadyRegisteredForVATMossController.onSubmit(), "VatMossUkVatRegistered")

  private def view(form: Form[_] = form)(implicit request: ServiceInfoRequest[_]): Html =
    alreadyRegisteredForVATMoss(appConfig, form, viewAction)(request.serviceInfoContent)

  def onPageLoad: Action[AnyContent] = (authenticate andThen serviceInfo) { implicit request =>
    Ok(view())
  }

  def onSubmit: Action[AnyContent] = (authenticate andThen serviceInfo) { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => BadRequest(view(formWithErrors)),
        value => Redirect(navigator.nextPage(UkBased, value))
      )
  }
}
