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

package controllers.employer

import javax.inject.Inject

import config.FrontendAppConfig
import controllers.actions._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import forms.employer.WhatEmployerTaxDoYouWantToAddFormProvider
import identifiers.WhatEmployerTaxDoYouWantToAddId
import models.employer.WhatEmployerTaxDoYouWantToAdd
import models.employer.WhatEmployerTaxDoYouWantToAdd.{EPAYE, PS}
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.Enrolments
import utils.{Enumerable, Navigator, RadioOption}
import views.html.employer.whatEmployerTaxDoYouWantToAdd

class WhatEmployerTaxDoYouWantToAddController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  navigator: Navigator[Call],
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: WhatEmployerTaxDoYouWantToAddFormProvider)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad() = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(
      whatEmployerTaxDoYouWantToAdd(appConfig, form, getOptions(request.request.enrolments))(
        request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData) { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          BadRequest(
            whatEmployerTaxDoYouWantToAdd(appConfig, formWithErrors, getOptions(request.request.enrolments))(
              request.serviceInfoContent)),
        (value) => Redirect(navigator.nextPage(WhatEmployerTaxDoYouWantToAddId, (value, request.request.enrolments)))
      )
  }

  def getOptions(enrolments: Enrolments): Seq[RadioOption] =
    checkPensionScheme(enrolments).intersect(checkEpayeEnrolment(enrolments))

  private def checkPensionScheme: Enrolments => Seq[RadioOption] = filterOptions(checkPension, PS)
  private def checkEpayeEnrolment: Enrolments => Seq[RadioOption] = filterOptions(checkEPaye, EPAYE)

  private def filterOptions(
    predicate: Enrolments => Boolean,
    option: WhatEmployerTaxDoYouWantToAdd): Enrolments => Seq[RadioOption] =
    (enrolments: Enrolments) =>
      if (predicate(enrolments)) {
        WhatEmployerTaxDoYouWantToAdd.options.filterNot(_.value == option.toString)
      } else {
        WhatEmployerTaxDoYouWantToAdd.options
    }

  private def checkPension: Enrolments => Boolean =
    e => checkPensionPractitionerScheme(e)

  private def checkPensionPractitionerScheme: Enrolments => Boolean =
    _.getEnrolment(utils.Enrolments.PP.toString).isDefined

  private def checkEPaye: Enrolments => Boolean =
    _.getEnrolment(utils.Enrolments.EPAYE.toString).isDefined

}
