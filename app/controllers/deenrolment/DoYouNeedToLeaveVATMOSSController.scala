/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.deenrolment.DoYouNeedToLeaveVATMOSSFormProvider
import handlers.ErrorHandler
import identifiers.DoYouNeedToLeaveVATMOSSId

import javax.inject.Inject
import models.deenrolment.DoYouNeedToLeaveVATMOSS
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.Enrolment
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enrolments, Enumerable, LoggingUtil, Navigator}
import views.html.deenrolment.doYouNeedToLeaveVATMOSS

class DoYouNeedToLeaveVATMOSSController @Inject()(appConfig: FrontendAppConfig,
                                                  mcc: MessagesControllerComponents,
                                                  navigator: Navigator[Either[String, Call]],
                                                  authenticate: AuthAction,
                                                  serviceInfoData: ServiceInfoAction,
                                                  formProvider: DoYouNeedToLeaveVATMOSSFormProvider,
                                                  errorHandler: ErrorHandler,
                                                  doYouNeedToLeaveVATMOSS: doYouNeedToLeaveVATMOSS)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits with LoggingUtil{

  val form: Form[DoYouNeedToLeaveVATMOSS] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(doYouNeedToLeaveVATMOSS(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    form.bindFromRequest()
      .fold(

        formWithErrors => BadRequest(doYouNeedToLeaveVATMOSS(appConfig, formWithErrors)(request.serviceInfoContent)),
        value => {
          val nextPage = navigator.nextPage(DoYouNeedToLeaveVATMOSSId, (value, vatMossEnrolment))

          nextPage match {
            case Right(c) => Redirect(c)
            case Left(s) =>
              warnLog(s"cannot navigate to the next page: $s")
              InternalServerError(errorHandler.internalServerErrorTemplate)
          }
        }
      )
  }

  private def vatMossEnrolment(implicit r: ServiceInfoRequest[_]): Option[Enrolment] =
    r.request.enrolments.getEnrolment(Enrolments.VATMOSS.toString)
}
