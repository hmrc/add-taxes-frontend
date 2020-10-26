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

package controllers.sa

import config.FrontendAppConfig
import connectors.{DataCacheConnector, EnrolmentStoreProxyConnector}
import controllers.actions._
import forms.sa.SAUTRFormProvider
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.sa.SAUTR
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Navigator
import views.html.sa.enterSAUTR

import scala.concurrent.{ExecutionContext, Future}

class EnterSAUTRController @Inject()(appConfig: FrontendAppConfig,
                                     mcc: MessagesControllerComponents,
                                     navigator: Navigator[Call],
                                     authenticate: AuthAction,
                                     serviceInfo: ServiceInfoAction,
                                     formProvider: SAUTRFormProvider,
                                     enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector,
                                     dataCacheConnector: DataCacheConnector,
                                     enterSAUTR: enterSAUTR)(implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  val pinAndPostFeatureToggle: Boolean = appConfig.pinAndPostFeatureToggle
  val form: Form[SAUTR] = formProvider()

  def onPageLoad: Action[AnyContent] = (authenticate andThen serviceInfo) { implicit request =>
    Ok(enterSAUTR(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit: Action[AnyContent] = (authenticate andThen serviceInfo).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => Future(BadRequest(enterSAUTR(appConfig, formWithErrors)(request.serviceInfoContent))),
        saUTR =>
          if(pinAndPostFeatureToggle){
          dataCacheConnector.save[SAUTR](request.request.externalId, EnterSAUTRId.toString, saUTR).flatMap { _ =>
            enrolmentStoreProxyConnector.checkExistingUTR(saUTR.value).map { enrolmentStoreResult =>
              Redirect(navigator.nextPage(EnterSAUTRId, enrolmentStoreResult))
            }
          }} else {
            enrolmentStoreProxyConnector.checkExistingUTR(saUTR.value).map { enrolmentStoreResult =>
              Redirect(navigator.nextPage(EnterSAUTRId, enrolmentStoreResult))
            }
          })
  }
}
