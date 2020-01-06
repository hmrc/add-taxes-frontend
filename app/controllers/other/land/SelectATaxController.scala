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

package controllers.other.land

import javax.inject.Inject

import config.FrontendAppConfig
import controllers.actions._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enumerable, HmrcEnrolmentType, Navigator}
import forms.other.land.SelectATaxFormProvider
import identifiers.SelectATaxId
import models.other.land.SelectATax
import models.requests.ServiceInfoRequest
import play.api.mvc.{AnyContent, Call}
import views.html.other.land.selectATax

class SelectATaxController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  navigator: Navigator[Call],
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: SelectATaxFormProvider)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  val optionsWithoutSDLT = SelectATax.options.filterNot(_.value == SelectATax.SDLT.toString)

  def radioOptions(implicit request: ServiceInfoRequest[AnyContent]) =
    request.request.enrolments match {
      case HmrcEnrolmentType.SDLT() => optionsWithoutSDLT
      case _                        => SelectATax.options
    }

  def onPageLoad() = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(selectATax(appConfig, form, radioOptions)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData) { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          BadRequest(selectATax(appConfig, formWithErrors, radioOptions)(request.serviceInfoContent)),
        (value) => Redirect(navigator.nextPage(SelectATaxId, value))
      )
  }
}
