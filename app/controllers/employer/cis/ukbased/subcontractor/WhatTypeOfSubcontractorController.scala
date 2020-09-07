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

package controllers.employer.cis.ukbased.subcontractor

import config.FrontendAppConfig
import controllers.actions._
import forms.employer.cis.uk.subcontractor.WhatTypeOfSubcontractorFormProvider
import identifiers.WhatTypeOfSubcontractorId
import javax.inject.Inject
import models.employer.cis.uk.subcontractor.WhatTypeOfSubcontractor
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.employer.cis.ukbased.subcontractor.whatTypeOfSubcontractor

class WhatTypeOfSubcontractorController @Inject()(appConfig: FrontendAppConfig,
                                                  mcc: MessagesControllerComponents,
                                                  navigator: Navigator[Call],
                                                  authenticate: AuthAction,
                                                  serviceInfoData: ServiceInfoAction,
                                                  formProvider: WhatTypeOfSubcontractorFormProvider,
                                                  whatTypeOfSubcontractor: whatTypeOfSubcontractor)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[WhatTypeOfSubcontractor] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(whatTypeOfSubcontractor(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => BadRequest(whatTypeOfSubcontractor(appConfig, formWithErrors)(request.serviceInfoContent)),
        value => Redirect(navigator.nextPage(WhatTypeOfSubcontractorId, (value, request.request.enrolments)))
      )
  }
}
