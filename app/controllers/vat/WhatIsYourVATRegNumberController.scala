/*
 * Copyright 2025 HM Revenue & Customs
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
import config.featureToggles.FeatureSwitch.VATKnownFactsCheck
import config.featureToggles.FeatureToggleSupport
import controllers.actions.{AuthAction, ServiceInfoAction}
import forms.vat.WhatIsYourVATRegNumberFormProvider
import identifiers.WhatIsYourVATRegNumberId
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import service.KnownFactsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.vat.{vatAccountUnavailable, whatIsYourVATRegNumber}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhatIsYourVATRegNumberController @Inject() (appConfig: FrontendAppConfig,
                                                  mcc: MessagesControllerComponents,
                                                  navigator: Navigator[Call],
                                                  authenticate: AuthAction,
                                                  serviceInfoData: ServiceInfoAction,
                                                  formProvider: WhatIsYourVATRegNumberFormProvider,
                                                  vatAccountUnavailable: vatAccountUnavailable,
                                                  whatIsYourVATRegNumber: whatIsYourVATRegNumber,
                                                  knownFactsService: KnownFactsService)(implicit val ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with Enumerable.Implicits
    with FeatureToggleSupport {

  private def form: Form[String] = formProvider()

  private val isKnownFactsCheckEnabled: Boolean = isEnabled(VATKnownFactsCheck)(appConfig)

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(whatIsYourVATRegNumber(appConfig, form, isKnownFactsCheckEnabled)(request.serviceInfoContent))
  }

  def onPageLoadVatUnavailable(): Action[AnyContent] = authenticate { implicit request =>
    Ok(vatAccountUnavailable(appConfig))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    def handleErrorSubmission(formWithErrors: Form[String]): Future[Result] =
      Future.successful(BadRequest(whatIsYourVATRegNumber(appConfig, formWithErrors, isKnownFactsCheckEnabled)(request.serviceInfoContent)))

    def handleSuccessfulSubmission(submittedVrn: String): Future[Result] = {
      val checkMandationStatus: Future[Either[Result, Int]]   = knownFactsService.bypassOrCheckMandationStatus(submittedVrn)
      val submittedVrnIsValid: Future[Either[Result, String]] = knownFactsService.checkVrnMatchesPreviousAttempts(submittedVrn)
      for {
        mandationStatus <- checkMandationStatus
        submittedVrn    <- submittedVrnIsValid
      } yield (mandationStatus, submittedVrn) match {
        case (Left(errorRedirect), _) => errorRedirect
        case (_, Left(errorRedirect)) => errorRedirect
        case (Right(status), Right(vrn)) =>
          Redirect(navigator.nextPage(WhatIsYourVATRegNumberId, (status, vrn)))
      }
    }

    form
      .bindFromRequest()
      .fold(
        formWithErrors => handleErrorSubmission(formWithErrors),
        vrn => handleSuccessfulSubmission(vrn)
      )
  }

}
