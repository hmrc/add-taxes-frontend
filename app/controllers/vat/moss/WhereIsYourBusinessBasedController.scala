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

package controllers.vat.moss

import config.FrontendAppConfig
import controllers.actions._
import forms.vat.moss.WhereIsYourBusinessBasedFormProvider
import identifiers.WhereIsYourBusinessBasedId
import javax.inject.Inject
import models.vat.moss.WhereIsYourBusinessBased
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.vat.moss.whereIsYourBusinessBased

import scala.concurrent.Future

class WhereIsYourBusinessBasedController @Inject()(appConfig: FrontendAppConfig,
                                                   mcc: MessagesControllerComponents,
                                                   navigator: Navigator[Call],
                                                   authenticate: AuthAction,
                                                   serviceInfoData: ServiceInfoAction,
                                                   formProvider: WhereIsYourBusinessBasedFormProvider,
                                                   whereIsYourBusinessBased: whereIsYourBusinessBased)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[WhereIsYourBusinessBased] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(whereIsYourBusinessBased(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(whereIsYourBusinessBased(appConfig, formWithErrors)(request.serviceInfoContent))),
        value => Future.successful(Redirect(navigator.nextPage(WhereIsYourBusinessBasedId, value)))
      )
  }
}
