/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.other.importexports.dan

import config.FrontendAppConfig
import controllers.actions._
import forms.other.importexports.dan.DoYouHaveDANFormProvider
import identifiers.DoYouHaveDANId
import javax.inject.Inject
import models.other.importexports.dan.DoYouHaveDAN
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.other.importexports.dan.doYouHaveDAN

import scala.concurrent.Future

class DoYouHaveDANController @Inject()(appConfig: FrontendAppConfig,
                                       mcc: MessagesControllerComponents,
                                       navigator: Navigator[Call],
                                       authenticate: AuthAction,
                                       serviceInfoData: ServiceInfoAction,
                                       formProvider: DoYouHaveDANFormProvider,
                                       doYouHaveDAN: doYouHaveDAN)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[DoYouHaveDAN] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(doYouHaveDAN(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(doYouHaveDAN(appConfig, formWithErrors)(request.serviceInfoContent))),
        value => Future.successful(Redirect(navigator.nextPage(DoYouHaveDANId, value)))
      )
  }
}
