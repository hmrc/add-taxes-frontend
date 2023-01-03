/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.other.ppt

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.PptSwitch
import config.featureToggles.FeatureToggleSupport.isEnabled
import controllers.actions.{AuthAction, ServiceInfoAction}
import handlers.ErrorHandler
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.other.ppt.you_need_a_ppt_ref

class YouNeedAPptRefController @Inject()(mcc: MessagesControllerComponents,
                                         errorHandler: ErrorHandler,
                                         authenticate: AuthAction,
                                         serviceInfoData: ServiceInfoAction,
                                         youNeedAPptRefView: you_need_a_ppt_ref,
                                         implicit val appConfig: FrontendAppConfig)
  extends FrontendController(mcc) with I18nSupport {

  val pptFeatureSwitch: Boolean = isEnabled(PptSwitch)

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    if(pptFeatureSwitch) {
      Ok(youNeedAPptRefView(appConfig)(request.serviceInfoContent))
    } else {
      InternalServerError(errorHandler.internalServerErrorTemplate)
    }
  }

}
