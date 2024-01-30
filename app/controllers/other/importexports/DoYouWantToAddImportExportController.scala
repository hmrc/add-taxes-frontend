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

package controllers.other.importexports

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.{ARSContentSwitch, AtarSwitch}
import config.featureToggles.FeatureToggleSupport.isEnabled
import controllers.actions._
import forms.other.importexports.DoYouWantToAddImportExportFormProvider
import identifiers.DoYouWantToAddImportExportId

import javax.inject.Inject
import models.other.importexports.DoYouWantToAddImportExport
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import service.AuditService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Enrolments.CommonTransitConvention
import utils.{Enumerable, Navigator}
import views.html.other.importexports.doYouWantToAddImportExport

import scala.concurrent.{ExecutionContext, Future}

class DoYouWantToAddImportExportController @Inject()(mcc: MessagesControllerComponents,
                                                     navigator: Navigator[Call],
                                                     authenticate: AuthAction,
                                                     serviceInfoData: ServiceInfoAction,
                                                     formProvider: DoYouWantToAddImportExportFormProvider,
                                                     doYouWantToAddImportExport: doYouWantToAddImportExport,
                                                     auditService: AuditService)
                                                    (implicit val ec: ExecutionContext, appConfig: FrontendAppConfig)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[DoYouWantToAddImportExport] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request: ServiceInfoRequest[_] =>

    Ok(doYouWantToAddImportExport(form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(doYouWantToAddImportExport(formWithErrors)(request.serviceInfoContent))),
        value => {auditService.auditSelectIOCategory(request.request.credId, value, request.request.enrolments)
          Future.successful(Redirect(navigator.nextPage(DoYouWantToAddImportExportId, value)))}
      )
  }
}
