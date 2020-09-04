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

package controllers.employer.paye

import config.FrontendAppConfig
import controllers.actions._
import forms.employer.paye.DoesBusinessHave1To9DirectorsFormProvider
import identifiers.DoesBusinessHave1To9DirectorsId
import javax.inject.Inject
import models.employer.paye.DoesBusinessHave1To9Directors
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.employer.paye.doesBusinessHave1To9Directors

class DoesBusinessHave1To9DirectorsController @Inject()(appConfig: FrontendAppConfig,
                                                        mcc: MessagesControllerComponents,
                                                        navigator: Navigator[Call],
                                                        authenticate: AuthAction,
                                                        serviceInfoData: ServiceInfoAction,
                                                        formProvider: DoesBusinessHave1To9DirectorsFormProvider,
                                                        doesBusinessHave1To9Directors: doesBusinessHave1To9Directors)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[DoesBusinessHave1To9Directors] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(doesBusinessHave1To9Directors(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => BadRequest(doesBusinessHave1To9Directors(appConfig, formWithErrors)(request.serviceInfoContent)),
        value => Redirect(navigator.nextPage(DoesBusinessHave1To9DirectorsId, value))
      )
  }
}
