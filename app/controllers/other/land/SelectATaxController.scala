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

package controllers.other.land

import config.FrontendAppConfig
import controllers.actions._
import forms.other.land.SelectATaxFormProvider
import identifiers.SelectATaxId
import javax.inject.Inject
import models.other.land.SelectATax
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, HmrcEnrolmentType, Navigator, RadioOption}
import views.html.other.land.selectATax

class SelectATaxController @Inject()(appConfig: FrontendAppConfig,
                                     mcc: MessagesControllerComponents,
                                     navigator: Navigator[Call],
                                     authenticate: AuthAction,
                                     serviceInfoData: ServiceInfoAction,
                                     formProvider: SelectATaxFormProvider,
                                     selectATax: selectATax)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[SelectATax] = formProvider()

  val optionsWithoutSDLT: Set[RadioOption] = SelectATax.options.filterNot(_.value == SelectATax.SDLT.toString)

  private def radioOptions(implicit request: ServiceInfoRequest[AnyContent]): Set[RadioOption] =
    request.request.enrolments match {
      case HmrcEnrolmentType.SDLT() => optionsWithoutSDLT
      case _                        => SelectATax.options
    }

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(selectATax(appConfig, form, radioOptions)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => BadRequest(selectATax(appConfig, formWithErrors, radioOptions)(request.serviceInfoContent)),
        value => Redirect(navigator.nextPage(SelectATaxId, value))
      )
  }
}
