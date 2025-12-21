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
import controllers.actions._
import forms.vat.ImportedGoodsFormProvider
import identifiers.ImportedGoodsId

import javax.inject.Inject
import models.vat.ImportedGoods
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import service.ThresholdService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.vat.importedGoods

class ImportedGoodsController @Inject()(appConfig: FrontendAppConfig,
                                        mcc: MessagesControllerComponents,
                                        navigator: Navigator[Call],
                                        authenticate: AuthAction,
                                        serviceInfoData: ServiceInfoAction,
                                        formProvider: ImportedGoodsFormProvider,
                                        importedGoods: importedGoods,
                                        thresholdService: ThresholdService)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  def onPageLoad(): Action[AnyContent] = {
    (authenticate andThen serviceInfoData) { implicit request =>
      val form: Form[ImportedGoods] = formProvider(thresholdService.formattedVatThreshold())
      Ok(importedGoods(appConfig, form, thresholdService.formattedVatThreshold())(request.serviceInfoContent))
    }
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    val form: Form[ImportedGoods] = formProvider(thresholdService.formattedVatThreshold())
    form.bindFromRequest()
      .fold(
        formWithErrors => BadRequest(importedGoods(appConfig, formWithErrors, thresholdService.formattedVatThreshold())(request.serviceInfoContent)),
        value => Redirect(navigator.nextPage(ImportedGoodsId, value))
      )
  }
}
