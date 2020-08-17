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

package controllers.employer.ers

import config.FrontendAppConfig
import controllers.actions._
import forms.employer.DoesBusinessManagePAYEFormProvider
import identifiers.DoesBusinessManagePAYEId
import javax.inject.Inject
import models.employer.DoesBusinessManagePAYE
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import viewmodels.ViewAction
import views.html.employer.doesBusinessManagePAYE

import scala.concurrent.Future

class DoesBusinessManagePAYEController @Inject()(appConfig: FrontendAppConfig,
                                                 mcc: MessagesControllerComponents,
                                                 navigator: Navigator[Call],
                                                 authenticate: AuthAction,
                                                 serviceInfoData: ServiceInfoAction,
                                                 formProvider: DoesBusinessManagePAYEFormProvider,
                                                 doesBusinessManagePAYE: doesBusinessManagePAYE)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  lazy val viewAction: ViewAction = ViewAction(routes.DoesBusinessManagePAYEController.onSubmit(), "AddErsEpayeOnline")
  val form: Form[DoesBusinessManagePAYE] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(doesBusinessManagePAYE(appConfig, form, viewAction)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(doesBusinessManagePAYE(appConfig, formWithErrors, viewAction)(request.serviceInfoContent))),
        value => Future.successful(Redirect(navigator.nextPage(DoesBusinessManagePAYEId.ERS, value)))
      )
  }
}
