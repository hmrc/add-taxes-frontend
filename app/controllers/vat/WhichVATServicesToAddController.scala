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

package controllers.vat

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enumerable, Navigator}

import forms.vat.WhichVATServicesToAddFormProvider
import identifiers.WhichVATServicesToAddId
import views.html.vat.whichVATServicesToAdd

import scala.concurrent.Future

class WhichVATServicesToAddController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: WhichVATServicesToAddFormProvider)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad() = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(whichVATServicesToAdd(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(whichVATServicesToAdd(appConfig, formWithErrors)(request.serviceInfoContent))),
        (value) => Future.successful(Redirect(navigator.nextPage(WhichVATServicesToAddId, value)))
      )
  }
}
