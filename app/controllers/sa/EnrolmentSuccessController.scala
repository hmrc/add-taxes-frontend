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

package controllers.sa

import config.FrontendAppConfig
import controllers.actions.{AuthAction, ServiceInfoAction}
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.sa.successfulEnrolment

import scala.concurrent.Future

class EnrolmentSuccessController @Inject()(authenticate: AuthAction,
                                           appConfig: FrontendAppConfig,
                                           serviceInfoData: ServiceInfoAction,
                                           mcc: MessagesControllerComponents,
                                           successfulEnrolment: successfulEnrolment)
  extends FrontendController(mcc) with I18nSupport  {

  val pinAndPostFeatureToggle: Boolean = appConfig.pinAndPostFeatureToggle

  def onPageLoad(origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData).async {
    implicit request =>
      if(pinAndPostFeatureToggle) {
        Future.successful(Ok(successfulEnrolment(appConfig, origin)(request.serviceInfoContent)))
      } else {
        Future.successful(Redirect(Call("GET", appConfig.getBusinessAccountUrl("home"))))
      }
  }

}
