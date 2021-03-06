/*
 * Copyright 2021 HM Revenue & Customs
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
import controllers.actions._
import forms.other.importexports.DoYouWantToAddImportExportFormProvider
import identifiers.DoYouWantToAddImportExportId
import javax.inject.Inject
import models.other.importexports.DoYouWantToAddImportExport
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.other.importexports.doYouWantToAddImportExport

import scala.concurrent.Future

class DoYouWantToAddImportExportController @Inject()(appConfig: FrontendAppConfig,
                                                     mcc: MessagesControllerComponents,
                                                     navigator: Navigator[Call],
                                                     authenticate: AuthAction,
                                                     serviceInfoData: ServiceInfoAction,
                                                     formProvider: DoYouWantToAddImportExportFormProvider,
                                                     doYouWantToAddImportExport: doYouWantToAddImportExport)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[DoYouWantToAddImportExport] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(doYouWantToAddImportExport(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(doYouWantToAddImportExport(appConfig, formWithErrors)(request.serviceInfoContent))),
        value => Future.successful(Redirect(navigator.nextPage(DoYouWantToAddImportExportId, value)))
      )
  }
}
