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
import forms.deenrolment.DoYouNeedToStopVatMossNUFormProvider
import handlers.ErrorHandler
import identifiers.DoYouNeedToStopVatMossNUId
import javax.inject.Inject
import models.deenrolment.DoYouNeedToStopVatMossNU
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.Enrolment
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enrolments, Enumerable, LoggingHelper, Navigator}
import views.html.deenrolment.doYouNeedToStopVatMossNU

class DoYouNeedToStopVatMossNUController @Inject()(appConfig: FrontendAppConfig,
                                                   mcc: MessagesControllerComponents,
                                                   navigator: Navigator[Either[String, Call]],
                                                   authenticate: AuthAction,
                                                   serviceInfoData: ServiceInfoAction,
                                                   formProvider: DoYouNeedToStopVatMossNUFormProvider,
                                                   errorHandler: ErrorHandler,
                                                   log: LoggingHelper,
                                                   doYouNeedToStopVatMossNU: doYouNeedToStopVatMossNU)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[DoYouNeedToStopVatMossNU] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(doYouNeedToStopVatMossNU(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => BadRequest(doYouNeedToStopVatMossNU(appConfig, formWithErrors)(request.serviceInfoContent)),
        value => {
          val nextPage = navigator.nextPage(DoYouNeedToStopVatMossNUId, (value, vatMossNUEnrolment))

          nextPage match {
            case Right(c) => Redirect(c)
            case Left(s) =>
              log.warn(s"cannot navigate to the next page: $s")
              InternalServerError(errorHandler.internalServerErrorTemplate)
          }
        }
      )
  }

  private def vatMossNUEnrolment(implicit r: ServiceInfoRequest[_]): Option[Enrolment] =
    r.request.enrolments.getEnrolment(Enrolments.VATMOSSNonUnion.toString)

}
