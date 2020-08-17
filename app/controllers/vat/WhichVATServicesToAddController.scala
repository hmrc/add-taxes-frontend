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

package controllers.vat

import config.FrontendAppConfig
import controllers.actions._
import forms.vat.WhichVATServicesToAddFormProvider
import identifiers.WhichVATServicesToAddId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.vat.WhichVATServicesToAdd
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, HmrcEnrolmentType, Navigator, RadioOption}
import views.html.vat.whichVATServicesToAdd

class WhichVATServicesToAddController @Inject()(appConfig: FrontendAppConfig,
                                                mcc: MessagesControllerComponents,
                                                navigator: Navigator[Call],
                                                authenticate: AuthAction,
                                                serviceInfoData: ServiceInfoAction,
                                                formProvider: WhichVATServicesToAddFormProvider,
                                                whichVATServicesToAdd: whichVATServicesToAdd)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[WhichVATServicesToAdd] = formProvider()
  val optionsWithoutVAT: Seq[RadioOption] =
    WhichVATServicesToAdd.options.filterNot(_.value == WhichVATServicesToAdd.VAT.toString)

  private def radioOptions(implicit request: ServiceInfoRequest[AnyContent]): Seq[RadioOption] =
    request.request.enrolments match {
      case HmrcEnrolmentType.VAT() => optionsWithoutVAT
      case _                       => WhichVATServicesToAdd.options
    }

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(whichVATServicesToAdd(appConfig, form, radioOptions)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => BadRequest(whichVATServicesToAdd(appConfig, formWithErrors, radioOptions)(request.serviceInfoContent)),
        value => Redirect(navigator.nextPage(WhichVATServicesToAddId, (value, request.request.affinityGroup, request.request.enrolments)))
      )
  }
}
