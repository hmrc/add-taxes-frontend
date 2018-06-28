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

package controllers.other.oil

import javax.inject.Inject

import config.FrontendAppConfig
import controllers.actions._
import forms.other.oils.SelectAnOilServiceFormProvider
import identifiers.SelectAnOilServiceId
import models.other.oil.SelectAnOilService.{RebatedOilsEnquiryService, TiedOilsEnquiryService}
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.AnyContent
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enrolments, Enumerable, Navigator, RadioOption}
import views.html.other.oil.selectAnOilService

import scala.concurrent.Future

class SelectAnOilServiceController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  navigator: Navigator,
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: SelectAnOilServiceFormProvider)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  def getOptions(implicit r: ServiceInfoRequest[AnyContent]): Seq[RadioOption] = {
    val checks = Seq(checkRebatedOil, checkTiedOil)
    checks.flatMap(_.apply(r.request.enrolments))
  }

  private def checkRebatedOil: (uk.gov.hmrc.auth.core.Enrolments) => Option[RadioOption] =
    _.getEnrolment(Enrolments.RebatedOils.toString)
      .fold[Option[RadioOption]](Some(RebatedOilsEnquiryService.toRadioOption))(_ => None)

  private def checkTiedOil: (uk.gov.hmrc.auth.core.Enrolments) => Option[RadioOption] =
    _.getEnrolment(Enrolments.TiedOils.toString)
      .fold[Option[RadioOption]](Some(TiedOilsEnquiryService.toRadioOption))(_ => None)

  def onPageLoad() = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(selectAnOilService(appConfig, form, getOptions)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          Future.successful(
            BadRequest(selectAnOilService(appConfig, formWithErrors, getOptions)(request.serviceInfoContent))),
        (value) => Future.successful(Redirect(navigator.nextPage(SelectAnOilServiceId, value)))
      )
  }
}
