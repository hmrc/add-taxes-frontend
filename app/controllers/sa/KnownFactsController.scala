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

import config.FrontendAppConfig
import connectors.{DataCacheConnector, EnrolmentStoreProxyConnector}
import controllers.actions._
import forms.sa.KnownFactsFormProvider
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.sa.{KnownFacts, SAUTR}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, UserAnswers}
import views.html.sa.knownFacts

import scala.concurrent.{ExecutionContext, Future}

class KnownFactsController @Inject()(
                                      appConfig: FrontendAppConfig,
                                      mcc: MessagesControllerComponents,
                                      authenticate: AuthAction,
                                      serviceInfoData: ServiceInfoAction,
                                      formProvider: KnownFactsFormProvider,
                                      dataCacheConnector: DataCacheConnector,
                                      enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector,
                                      knownFactsPage: knownFacts)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  implicit val ec: ExecutionContext = mcc.executionContext
  val form: Form[KnownFacts] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) {
    implicit request =>
      Ok(knownFactsPage(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[KnownFacts]) => {
          Future(BadRequest(knownFactsPage(appConfig, formWithErrors)(request.serviceInfoContent)))
        },
        (value) => {
          val queryKnownFactsBoolean: Future[Boolean] = dataCacheConnector.getEntry[SAUTR](request.request.externalId, EnterSAUTRId.toString)
            .flatMap { maybeSAUTR =>
              (
                for {
                  utr <- maybeSAUTR
                } yield enrolmentStoreProxyConnector.queryKnownFacts(utr, value)
              ).getOrElse(Future.successful(false))
            }

          queryKnownFactsBoolean.map {
            case true => Redirect(controllers.routes.LanguageSwitchController.switchToLanguage("en"))
            case false => Redirect(controllers.routes.LanguageSwitchController.switchToLanguage("cy"))
            case _ => Redirect(controllers.routes.LanguageSwitchController.switchToLanguage("cy"))
          }
        }
      )
  }
}
