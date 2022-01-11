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

package controllers.sa

import config.FrontendAppConfig
import controllers.actions.{AuthAction, ServiceInfoAction}
import forms.sa.KnownFactsPostcodeFormProvider
import javax.inject.Inject
import models.sa.{KnownFacts, KnownFactsPostcode}
import play.api.data.{Form, FormError}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.KnownFactsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Enumerable
import views.html.sa.postcodeKnownFacts

import scala.concurrent.{ExecutionContext, Future}

class PostcodeController@Inject()(
                                   appConfig: FrontendAppConfig,
                                   mcc: MessagesControllerComponents,
                                   authenticate: AuthAction,
                                   serviceInfoData: ServiceInfoAction,
                                   formProvider: KnownFactsPostcodeFormProvider,
                                   postcodePage: postcodeKnownFacts,
                                   knownFactsService: KnownFactsService
                                 )
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  implicit val ec: ExecutionContext = mcc.executionContext
  val form: Form[KnownFactsPostcode] = formProvider()

  def onPageLoad(origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData) {
    implicit request =>
        Ok(postcodePage(appConfig, form, origin)(request.serviceInfoContent))
  }

  def onSubmit(origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(postcodePage(appConfig, formWithErrors, origin)(request.serviceInfoContent)))
        },
        value =>
          (value.kfPostcode, value.kfAbroad) match {
            case (Some(_), Some(_)) => Future(BadRequest(postcodePage(appConfig, form.withError(FormError("postcode", "enterKnownFacts.postcode.error.required")).withError(FormError("isAbroad", "enterKnownFacts.postcode.error.required")), origin)(request.serviceInfoContent)))
            case (None, None)       => Future(BadRequest(postcodePage(appConfig, form.withError(FormError("postcode", "enterKnownFacts.postcode.error.required")).withError(FormError("isAbroad", "enterKnownFacts.postcode.error.required")), origin)(request.serviceInfoContent)))
            case _                  => knownFactsService.knownFactsLocation(KnownFacts(value.kfPostcode, None, value.kfAbroad), origin)
          }
      )
  }
}
