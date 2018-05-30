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

package controllers.employer.intermediaries

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import forms.employer.DoesBusinessManagePAYEFormProvider
import identifiers.DoesBusinessManagePAYEId
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enumerable, Navigator}
import viewmodels.ViewAction
import views.html.employer.doesBusinessManagePAYE

import scala.concurrent.Future

class DoesBusinessManagePAYEController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: DoesBusinessManagePAYEFormProvider)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  lazy val action = ViewAction(routes.DoesBusinessManagePAYEController.onSubmit(), "AddIntermediariesEpayeOnline")

  def onPageLoad() = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(doesBusinessManagePAYE(appConfig, form, action)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          Future.successful(
            BadRequest(doesBusinessManagePAYE(appConfig, formWithErrors, action)(request.serviceInfoContent))),
        (value) => Future.successful(Redirect(navigator.nextPage(DoesBusinessManagePAYEId.EI, value)))
      )
  }
}
