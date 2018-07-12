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
import models.employer.WhatEmployerTaxDoYouWantToAdd.EPAYE
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import utils.{Enumerable, Navigator, RadioOption}
import views.html.employer.whatEmployerTaxDoYouWantToAdd

class WhatEmployerTaxDoYouWantToAddController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  navigator: Navigator,
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: WhatEmployerTaxDoYouWantToAddFormProvider)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  val optionsWithoutPensionScheme =
    WhatEmployerTaxDoYouWantToAdd.options.filterNot(_.value == WhatEmployerTaxDoYouWantToAdd.PS.toString)

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

  def getOptions(enrolments: Enrolments): Seq[RadioOption] = {
    val pensionSchemeOption = checkPensionScheme(enrolments)

    checkEpayeEnrolment(enrolments, pensionSchemeOption)
  }

  private def checkPensionScheme: (Enrolments) => Seq[RadioOption] =
    (enrolments: Enrolments) =>
      if (checkPension(enrolments)) {
        WhatEmployerTaxDoYouWantToAdd.options.filterNot(_.value == WhatEmployerTaxDoYouWantToAdd.PS.toString)
      } else {
        WhatEmployerTaxDoYouWantToAdd.options
    }

  private def checkEpayeEnrolment: (Enrolments, Seq[RadioOption]) => Seq[RadioOption] =
    (enrolments: Enrolments, radioOption: Seq[RadioOption]) =>
      if (checkEPaye(enrolments)) {
        radioOption.filterNot(_.value == EPAYE.toString)
      } else {
        radioOption
    }

  private def checkPension: Enrolments => Boolean =
    e => checkPensionAdministratorScheme(e) && checkPensionPractitionerScheme(e)

  private def checkPensionAdministratorScheme: (Enrolments) => Boolean =
    _.getEnrolment(utils.Enrolments.PSA.toString).isDefined

  private def checkPensionPractitionerScheme: (Enrolments) => Boolean =
    _.getEnrolment(utils.Enrolments.PP.toString).isDefined

  private def checkEPaye: (Enrolments) => Boolean =
    _.getEnrolment(utils.Enrolments.EPAYE.toString).isDefined

}
