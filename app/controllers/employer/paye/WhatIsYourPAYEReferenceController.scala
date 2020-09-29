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

package controllers.employer.paye

import config.FrontendAppConfig
import connectors.EnrolmentStoreProxyConnector
import controllers.actions.{AuthAction, ServiceInfoAction}
import forms.employer.paye.WhatIsYourPAYEReferenceFormProvider
import identifiers.EnterPAYEReferenceId
import javax.inject.Inject
import models.employer.paye.PAYEReference
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enrolments, Navigator}
import views.html.employer.paye.whatIsYourPAYEReference

import scala.concurrent.{ExecutionContext, Future}

class WhatIsYourPAYEReferenceController @Inject()(appConfig: FrontendAppConfig,
                                                  whatIsYourPAYEReference: whatIsYourPAYEReference,
                                                  mcc: MessagesControllerComponents,
                                                  navigator: Navigator[Call],
                                                  authenticate: AuthAction,
                                                  serviceInfoData: ServiceInfoAction,
                                                  enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector,
                                                  formProvider: WhatIsYourPAYEReferenceFormProvider)
                                                 (implicit val ec: ExecutionContext)
  extends FrontendController(mcc)
    with I18nSupport {

  val enrolmentCheckerFeature = appConfig.epayeEnrolmentCheckerEnabled

  val form: Form[PAYEReference] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    if(enrolmentCheckerFeature) {
      Ok(whatIsYourPAYEReference(appConfig, form)(request.serviceInfoContent))
    } else {
      Redirect(Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.EPAYE)))
    }
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => Future(BadRequest(whatIsYourPAYEReference(appConfig, formWithErrors)(request.serviceInfoContent))),
        empRef =>
          enrolmentStoreProxyConnector.checkExistingEmpRef(empRef.officeNumber, empRef.payeReference).map { enrolmentStoreResult =>
            Redirect(navigator.nextPage(EnterPAYEReferenceId, enrolmentStoreResult))
          }
      )
  }

}
