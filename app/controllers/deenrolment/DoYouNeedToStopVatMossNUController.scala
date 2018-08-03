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

package controllers.deenrolment

import javax.inject.Inject

import config.FrontendAppConfig
import controllers.actions._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Call
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enrolments, Enumerable, Navigator}
import forms.deenrolment.DoYouNeedToStopVatMossNUFormProvider
import identifiers.DoYouNeedToStopVatMossNUId
import views.html.deenrolment.doYouNeedToStopVatMossNU
import models.requests.ServiceInfoRequest
import scala.concurrent.Future
import utils.LoggingHelper
import controllers.actions._
import handlers.ErrorHandler

class DoYouNeedToStopVatMossNUController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  navigator: Navigator[Either[String, Call]],
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: DoYouNeedToStopVatMossNUFormProvider,
  errorHandler: ErrorHandler,
  log: LoggingHelper)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad() = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(doYouNeedToStopVatMossNU(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData) { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          BadRequest(doYouNeedToStopVatMossNU(appConfig, formWithErrors)(request.serviceInfoContent)),
        (value) => {
          val nextPage = navigator.nextPage(DoYouNeedToStopVatMossNUId, (value, vatMossNUEnrolment))
          nextPage match {
            case Right(c) => Redirect(c)
            case Left(s) => {
              log.warn(s"cannot navigate to the next page: $s")
              InternalServerError(errorHandler.internalServerErrorTemplate)
            }
          }
        }
      )
  }

  def vatMossNUEnrolment(implicit r: ServiceInfoRequest[_]) =
    r.request.enrolments.getEnrolment(Enrolments.VATMOSSNonUnion.toString)

}
