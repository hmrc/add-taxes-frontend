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

package controllers.other.importexports.nes

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import controllers.actions._
import config.FrontendAppConfig
import forms.other.importexports.DoYouHaveEORINumberFormProvider
import identifiers.DoYouHaveEORINumberId
import play.api.data.Form
import utils.Navigator
import viewmodels.ViewAction
import views.html.other.importexports.doYouHaveEORINumber

import scala.concurrent.Future

class DoYouHaveEORINumberController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  navigator: Navigator,
  authenticate: AuthAction,
  serviceInfo: ServiceInfoAction,
  formProvider: DoYouHaveEORINumberFormProvider)
    extends FrontendController
    with I18nSupport {

  val form = formProvider()
  lazy val action = ViewAction(routes.DoYouHaveEORINumberController.onSubmit(), "AddNESTax")

  def onPageLoad = (authenticate andThen serviceInfo) { implicit request =>
    Ok(doYouHaveEORINumber(appConfig, form, action)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfo).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          Future.successful(
            BadRequest(doYouHaveEORINumber(appConfig, formWithErrors, action)(request.serviceInfoContent))
        ),
        value => Future.successful(Redirect(navigator.nextPage(DoYouHaveEORINumberId.NES, value)))
      )
  }
}
