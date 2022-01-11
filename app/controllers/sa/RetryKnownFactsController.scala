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
import connectors.DataCacheConnector
import controllers.actions.{AuthAction, ServiceInfoAction}
import forms.sa.KnownFactsNinoFormProvider
import javax.inject.Inject
import models.sa.KnownFactsNino
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.sa.retryKnownFacts

import scala.concurrent.ExecutionContext

class RetryKnownFactsController @Inject()(appConfig: FrontendAppConfig,
                                          authenticate: AuthAction,
                                          serviceInfoData: ServiceInfoAction,
                                          dataCacheConnector: DataCacheConnector,
                                          mcc: MessagesControllerComponents,
                                          retryKnownFacts: retryKnownFacts)(implicit val ec: ExecutionContext)
extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData) {
    implicit request =>
      val ninoExistsBoolean = request.request.nino.isDefined
        Ok(retryKnownFacts(appConfig, origin, ninoExistsBoolean)(request.serviceInfoContent))
  }

  def onSubmit(origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData) {
    implicit request =>
      dataCacheConnector.save[Boolean](request.request.credId, "tryAgain", true)
        Redirect(routes.EnterSAUTRController.onPageLoad(Some(origin)))
  }
}