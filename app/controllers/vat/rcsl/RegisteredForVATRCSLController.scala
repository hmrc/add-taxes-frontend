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

package controllers.vat.rcsl

import config.FrontendAppConfig
import controllers.actions._
import forms.vat.RegisteredForVATFormProvider
import identifiers.RegisteredForVATRCSLId
import javax.inject.Inject
import models.vat.RegisteredForVAT
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Navigator
import viewmodels.ViewAction
import views.html.vat.registeredForVAT

import scala.concurrent.Future

class RegisteredForVATRCSLController @Inject()(appConfig: FrontendAppConfig,
                                               mcc: MessagesControllerComponents,
                                               authenticate: AuthAction,
                                               navigator: Navigator[Call],
                                               serviceInfo: ServiceInfoAction,
                                               formProvider: RegisteredForVATFormProvider,
                                               registeredForVAT: registeredForVAT)
  extends FrontendController(mcc) with I18nSupport {

  val form: Form[RegisteredForVAT] = formProvider()
  lazy val viewAction: ViewAction = ViewAction(routes.RegisteredForVATRCSLController.onSubmit(), "VatRCSLNoVat")

  def onPageLoad: Action[AnyContent] = (authenticate andThen serviceInfo) { implicit request =>
    Ok(registeredForVAT(appConfig, form, viewAction)(request.serviceInfoContent))
  }

  def onSubmit: Action[AnyContent] = (authenticate andThen serviceInfo).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(registeredForVAT(appConfig, formWithErrors, viewAction)(request.serviceInfoContent))),
        value => Future.successful(Redirect(navigator.nextPage(RegisteredForVATRCSLId, value)))
      )
  }
}
