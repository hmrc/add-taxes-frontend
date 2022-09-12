/*
 * Copyright 2022 HM Revenue & Customs
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
import config.featureToggles.FeatureSwitch.VatOssSwitch
import config.featureToggles.FeatureToggleSupport
import connectors.OssConnector
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
                                                ossConnector: OssConnector,
                                                errorHandler: ErrorHandler,
                                                whichVATServicesToAdd: whichVATServicesToAdd,
                                                implicit val appConfig: FrontendAppConfig)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits with LoggingUtil with FeatureToggleSupport {

  implicit val ec: ExecutionContext = mcc.executionContext

  val form: Form[WhichVATServicesToAdd] = formProvider()
  val optionsWithoutVAT: Seq[RadioOption] =
    WhichVATServicesToAdd.options(ossFeatureSwitch = isEnabled(VatOssSwitch)).filterNot(_.value == WhichVATServicesToAdd.VAT.toString)

  private def radioOptions(implicit request: ServiceInfoRequest[AnyContent]): Seq[RadioOption] =
    request.request.enrolments match {
      case HmrcEnrolmentType.VAT() | HmrcEnrolmentType.MTDVAT() => optionsWithoutVAT
      case _                       => WhichVATServicesToAdd.options(ossFeatureSwitch = isEnabled(VatOssSwitch))
    }

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(whichVATServicesToAdd(appConfig, form, radioOptions)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(whichVATServicesToAdd(appConfig, formWithErrors, radioOptions)(request.serviceInfoContent))),
        value =>
          value match {
            case WhichVATServicesToAdd.VATOSS if (isEnabled(VatOssSwitch)) => {
              for (
                redirectString <- ossConnector.ossRegistrationJourneyLink()
              ) yield {
                redirectString.redirectUrl match {
                  case Some(url) => Redirect(navigator.nextPage(WhichVATServicesToAddId,
                      (value, request.request.affinityGroup,
                        request.request.enrolments,
                        appConfig.vatOssRedirectUrl(url))))
                  case _ =>
                    warnLog("[WhichVATServicesToAddController][onSubmit] No URL passed from OSS Call")
                    InternalServerError(errorHandler.internalServerErrorTemplate)
                }
              }
            }
            case _ => Future.successful(Redirect(navigator.nextPage(WhichVATServicesToAddId, (value, request.request.affinityGroup, request.request.enrolments, ""))))
          }
      )
  }
}
