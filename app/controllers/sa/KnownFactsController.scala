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

package controllers.sa

import config.{FeatureToggles, FrontendAppConfig}
import controllers.actions._
import forms.sa.KnownFactsFormProvider
import javax.inject.Inject
import models.sa.KnownFacts
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import service.KnownFactsService
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Enumerable
import views.html.sa.knownFacts

import scala.concurrent.{ExecutionContext, Future}

class KnownFactsController @Inject()(
                                      appConfig: FrontendAppConfig,
                                      mcc: MessagesControllerComponents,
                                      authenticate: AuthAction,
                                      serviceInfoData: ServiceInfoAction,
                                      formProvider: KnownFactsFormProvider,
                                      knownFactsPage: knownFacts,
                                      knownFactsService: KnownFactsService
                                    )
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  implicit val ec: ExecutionContext = mcc.executionContext
  val form: Form[KnownFacts] = formProvider()
  val pinAndPostFeatureToggle = appConfig.pinAndPostFeatureToggle

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) {
    implicit request =>
      if (pinAndPostFeatureToggle) {
        Ok(knownFactsPage(appConfig, form)(request.serviceInfoContent))
      } else {
        Redirect(Call("GET", appConfig.getBusinessAccountUrl("home")))
      }
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[KnownFacts]) => {
          Future(BadRequest(knownFactsPage(appConfig, formWithErrors)(request.serviceInfoContent)))
        },
        value => knownFactsService.knownFactsLocation(value)
      )
  }

}
