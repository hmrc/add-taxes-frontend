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

package controllers.sa

import config.FrontendAppConfig
import controllers.actions._
import forms.sa.YourSaIsNotInThisAccountFormProvider
import javax.inject.Inject
import models.sa.YourSaIsNotInThisAccount
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.SaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Enumerable
import views.html.sa.yourSaIsNotInThisAccount

import scala.concurrent.Future

class YourSaIsNotInThisAccountController @Inject()(appConfig: FrontendAppConfig,
                                                   mcc: MessagesControllerComponents,
                                                   authenticate: AuthAction,
                                                   serviceInfoData: ServiceInfoAction,
                                                   formProvider: YourSaIsNotInThisAccountFormProvider,
                                                   yourSaIsNotInThisAccount: yourSaIsNotInThisAccount,
                                                   saService: SaService)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  implicit val ec = mcc.executionContext
  val form: Form[YourSaIsNotInThisAccount] = formProvider()

  def onPageLoad(origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    if (request.session.get("tryingToAccessSa").contains("true")) {
      Ok(yourSaIsNotInThisAccount(appConfig, form, origin)(request.serviceInfoContent))
    } else {
      SeeOther(appConfig.getBusinessAccountUrl("home"))
    }
  }

  def onSubmit(origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => Future(BadRequest(yourSaIsNotInThisAccount(appConfig, formWithErrors, origin)(request.serviceInfoContent))),
        value => saService.yourSaIsNotInThisAccount(value, origin)
      )
  }

}
