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

package controllers.sa

import config.FrontendAppConfig
import controllers.actions._
import forms.sa.KnownFactsNinoFormProvider

import javax.inject.Inject
import models.sa.{KnownFacts, KnownFactsNino, KnownFactsPostcode}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.KnownFactsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Enumerable
import views.html.sa.knownFacts

import scala.concurrent.{ExecutionContext, Future}

class KnownFactsController @Inject()(
                                      appConfig: FrontendAppConfig,
                                      mcc: MessagesControllerComponents,
                                      authenticate: AuthAction,
                                      serviceInfoData: ServiceInfoAction,
                                      formProvider: KnownFactsNinoFormProvider,
                                      knownFactsPage: knownFacts,
                                      knownFactsService: KnownFactsService
                                    )
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  implicit val ec: ExecutionContext = mcc.executionContext
  val form: Form[KnownFactsNino] = formProvider()

  def onPageLoad(origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData) {
    implicit request =>
        Ok(knownFactsPage(appConfig, form, origin)(request.serviceInfoContent))
  }

  def onSubmit(origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[KnownFactsNino]) => {
          Future(BadRequest(knownFactsPage(appConfig, formWithErrors, origin)(request.serviceInfoContent)))
        },
        value => knownFactsService.knownFactsLocation(KnownFacts(None, Some(value.kfNino), None), origin)
      )
  }

}
