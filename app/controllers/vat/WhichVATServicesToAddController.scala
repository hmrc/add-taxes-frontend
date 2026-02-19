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

package controllers.vat

import config.FrontendAppConfig
import config.featureToggles.FeatureToggleSupport
import connectors.VatOneStopConnector
import controllers.actions._
import forms.vat.WhichVATServicesToAddFormProvider
import handlers.ErrorHandler
import identifiers.WhichVATServicesToAddId
import models.requests.ServiceInfoRequest
import models.vat.WhichVATServicesToAdd
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils._
import views.html.vat.whichVATServicesToAdd

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhichVATServicesToAddController @Inject()(mcc: MessagesControllerComponents,
                                                navigator: Navigator[Call],
                                                authenticate: AuthAction,
                                                serviceInfoData: ServiceInfoAction,
                                                formProvider: WhichVATServicesToAddFormProvider,
                                                vatOneStopConnector: VatOneStopConnector,
                                                errorHandler: ErrorHandler,
                                                whichVATServicesToAdd: whichVATServicesToAdd,
                                                implicit val appConfig: FrontendAppConfig)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits with LoggingUtil with FeatureToggleSupport {

  implicit val ec: ExecutionContext = mcc.executionContext

  val form: Form[WhichVATServicesToAdd] = formProvider()

  private def radioOptions(implicit request: ServiceInfoRequest[AnyContent]): Seq[RadioOption] = {
    val enrolments = request.request.enrolments
    WhichVATServicesToAdd.options
      .filterNot(x =>
        if (checkForVatEnrolment) {
          x.value == WhichVATServicesToAdd.VAT.toString
        } else {
          false
        }
      ).filterNot(x =>
      if (enrolments.getEnrolment("HMRC-OSS-ORG").isDefined) {
        x.value == WhichVATServicesToAdd.VATOSS.toString
      } else {
        false
      }
    )
  }

  private def checkForVatEnrolment(implicit request: ServiceInfoRequest[AnyContent]): Boolean = {
    val enrolments = request.request.enrolments
    if (enrolments.getEnrolment("HMRC-MTD-VAT").isDefined || enrolments.getEnrolment("HMCE-VATDEC-ORG").isDefined) {
      true
    } else {
      false
    }
  }

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(whichVATServicesToAdd(appConfig, form, radioOptions, checkForVatEnrolment)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(whichVATServicesToAdd(appConfig, formWithErrors, radioOptions, checkForVatEnrolment)(request.serviceInfoContent))),
        value =>
          value match {
            case _ if(value == WhichVATServicesToAdd.VATIOSS || value == WhichVATServicesToAdd.VATOSS) =>
              for (
                redirectString <- vatOneStopConnector.vatOneStopRegistrationJourneyLink(value, request.lang.language)
              ) yield {
                redirectString.redirectUrl match {
                  case Some(url) =>
                    infoLog(s"[WhichVATServicesToAddController][onSubmit] redirecting user to $url")
                    Redirect(navigator.nextPage(WhichVATServicesToAddId,
                      (value,
                        request.request.affinityGroup,
                        request.request.enrolments,
                        url)
                    ))
                  case _ =>
                    warnLog("[WhichVATServicesToAddController][onSubmit] No URL passed from Vat One Stop Call")
                    InternalServerError(errorHandler.internalServerErrorTemplate)
                }
              }
            case _ => Future.successful(Redirect(navigator.nextPage(WhichVATServicesToAddId, (value, request.request.affinityGroup, request.request.enrolments, ""))))
          }
      )
  }
}
