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

package controllers.other.ppt

import config.FrontendAppConfig
import controllers.actions.{AuthAction, ServiceInfoAction}
import forms.other.ppt.PptReferenceFormProvider
import handlers.ErrorHandler
import identifiers.DoYouHaveAPptRegId
import javax.inject.Inject
import models.other.ppt.DoYouHaveAPptReference
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Navigator
import views.html.other.ppt.do_you_have_a_ppt_reference

import scala.concurrent.Future

class DoYouHaveAPPTRefController @Inject()(appConfig: FrontendAppConfig,
                                           mcc: MessagesControllerComponents,
                                           navigator: Navigator[Call],
                                           errorHandler: ErrorHandler,
                                           authenticate: AuthAction,
                                           serviceInfoData: ServiceInfoAction,
                                           formProvider: PptReferenceFormProvider,
                                           doYouHaveAPPTRefView: do_you_have_a_ppt_reference)
  extends FrontendController(mcc) with I18nSupport {

  val pptFeatureSwitch: Boolean = appConfig.pptFeatureSwitch

  val form: Form[DoYouHaveAPptReference] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    if(pptFeatureSwitch) {
      Ok(doYouHaveAPPTRefView(appConfig, form)(request.serviceInfoContent))
    } else {
      InternalServerError(errorHandler.internalServerErrorTemplate)
    }
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(doYouHaveAPPTRefView(appConfig, formWithErrors)(request.serviceInfoContent))),
        value => Future.successful(Redirect(navigator.nextPage(DoYouHaveAPptRegId, value)))
      )
  }

}
