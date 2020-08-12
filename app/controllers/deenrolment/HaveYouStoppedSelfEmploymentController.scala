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

package controllers.deenrolment

import config.FrontendAppConfig
import controllers.actions._
import forms.deenrolment.HaveYouStoppedSelfEmploymentFormProvider
import identifiers.HaveYouStoppedSelfEmploymentId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import utils.{Enumerable, HmrcEnrolmentType, Navigator}
import forms.deenrolment.HaveYouStoppedSelfEmploymentFormProvider
import identifiers.HaveYouStoppedSelfEmploymentId
import models.deenrolment.HaveYouStoppedSelfEmployment
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.deenrolment.haveYouStoppedSelfEmployment

class HaveYouStoppedSelfEmploymentController @Inject()(appConfig: FrontendAppConfig,
                                                       mcc: MessagesControllerComponents,
                                                       navigator: Navigator[Call],
                                                       authenticate: AuthAction,
                                                       serviceInfoData: ServiceInfoAction,
                                                       formProvider: HaveYouStoppedSelfEmploymentFormProvider,
                                                       haveYouStoppedSelfEmployment: haveYouStoppedSelfEmployment)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[HaveYouStoppedSelfEmployment] = formProvider()

  private def redirectWhenHasCT[A](noRedirect: => Result)(implicit request: ServiceInfoRequest[A]): Result =
    request.request.enrolments match {
      case HmrcEnrolmentType.CORP_TAX() => Redirect(routes.StopFilingSelfAssessmentController.onPageLoad())
      case _                            => noRedirect
    }

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    redirectWhenHasCT {
      Ok(haveYouStoppedSelfEmployment(appConfig, form)(request.serviceInfoContent))
    }
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    redirectWhenHasCT {
      form.bindFromRequest()
        .fold(
          formWithErrors =>
            BadRequest(haveYouStoppedSelfEmployment(appConfig, formWithErrors)(request.serviceInfoContent)),
          value => Redirect(navigator.nextPage(HaveYouStoppedSelfEmploymentId, value))
        )
    }
  }
}
