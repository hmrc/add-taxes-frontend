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

package controllers.other.importexports.ncts

import config.FrontendAppConfig
import controllers.actions._
import controllers.other.importexports.ncts.routes._
import forms.other.importexports.DoYouHaveEORINumberFormProvider
import identifiers.DoYouHaveEORINumberId
import javax.inject.Inject
import models.other.importexports.DoYouHaveEORINumber
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import viewmodels.ViewAction
import views.html.other.importexports.doYouHaveEORINumber

import scala.concurrent.Future

class DoYouHaveEORINumberController @Inject()(appConfig: FrontendAppConfig,
                                              mcc: MessagesControllerComponents,
                                              navigator: Navigator[Call],
                                              authenticate: AuthAction,
                                              serviceInfoData: ServiceInfoAction,
                                              formProvider: DoYouHaveEORINumberFormProvider,
                                              doYouHaveEORINumber: doYouHaveEORINumber)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[DoYouHaveEORINumber] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(
      doYouHaveEORINumber(appConfig, form, ViewAction(DoYouHaveEORINumberController.onSubmit(), "AddNCTSTax"))(request.serviceInfoContent)
    )
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(
              doYouHaveEORINumber(
                appConfig,
                formWithErrors,
                ViewAction(DoYouHaveEORINumberController.onSubmit(), "AddNCTSTax"))(request.serviceInfoContent)
          )),
        value => Future.successful(Redirect(navigator.nextPage(DoYouHaveEORINumberId.NCTS, value)))
      )
  }
}
