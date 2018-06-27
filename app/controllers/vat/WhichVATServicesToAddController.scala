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

package controllers.vat

import config.FrontendAppConfig
import controllers.actions._
import forms.vat.WhichVATServicesToAddFormProvider
import identifiers.WhichVATServicesToAddId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.vat.WhichVATServicesToAdd
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.AnyContent
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enrolments, Enumerable, HmrcEnrolmentType, Navigator}
import views.html.vat.whichVATServicesToAdd

class WhichVATServicesToAddController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  navigator: Navigator,
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: WhichVATServicesToAddFormProvider)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()
  val optionsWithoutVAT = WhichVATServicesToAdd.options.filterNot(_.value == WhichVATServicesToAdd.VAT.toString)

  def radioOptions(implicit request: ServiceInfoRequest[AnyContent]) =
    request.request.enrolments match {
      case HmrcEnrolmentType.VAT() => optionsWithoutVAT
      case _                       => WhichVATServicesToAdd.options
    }

  def onPageLoad() = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(whichVATServicesToAdd(appConfig, form, radioOptions)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData) { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          BadRequest(whichVATServicesToAdd(appConfig, formWithErrors, radioOptions)(request.serviceInfoContent)),
        (value) =>
          Redirect(
            navigator
              .nextPage(WhichVATServicesToAddId, (value, request.request.affinityGroup, request.request.enrolments)))
      )
  }
}
