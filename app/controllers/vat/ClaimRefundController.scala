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

package controllers.vat

import javax.inject.Inject
import config.FrontendAppConfig
import controllers.actions._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Call
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enumerable, Navigator}
import forms.vat.ClaimRefundFormProvider
import identifiers.ClaimRefundId
import playconfig.featuretoggle.NewVatJourney
import views.html.vat.claimRefund

import scala.concurrent.Future

class ClaimRefundController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  navigator: Navigator[Call],
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: ClaimRefundFormProvider,
  featureDepandantAction: FeatureDependantAction)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad() = (authenticate andThen serviceInfoData andThen featureDepandantAction.permitFor(NewVatJourney)) {
    implicit request =>
      Ok(claimRefund(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData) { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) => BadRequest(claimRefund(appConfig, formWithErrors)(request.serviceInfoContent)),
        (value) => Redirect(navigator.nextPage(ClaimRefundId, value))
      )
  }
}
