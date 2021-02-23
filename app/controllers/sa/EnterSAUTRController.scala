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
import connectors.{DataCacheConnector, EnrolmentStoreProxyConnector}
import controllers.Assets.Redirect
import controllers.actions._
import forms.sa.SAUTRFormProvider
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.sa.{DoYouHaveSAUTR, EnrolmentCheckResult, SAUTR, SelectSACategory}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import play.libs.F.Tuple
import service.{AuditService, KnownFactsService, SelectSaCategoryService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Navigator
import views.html.sa.enterSAUTR

import scala.concurrent.{ExecutionContext, Future}

class EnterSAUTRController @Inject()(appConfig: FrontendAppConfig,
                                     mcc: MessagesControllerComponents,
                                     authenticate: AuthAction,
                                     serviceInfo: ServiceInfoAction,
                                     formProvider: SAUTRFormProvider,
                                     dataCacheConnector: DataCacheConnector,
                                     enterSAUTR: enterSAUTR,
                                     selectSaCategoryService: SelectSaCategoryService)(implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  val pinAndPostFeatureToggle: Boolean = appConfig.pinAndPostFeatureToggle
  val form: Form[SAUTR] = formProvider()

  def onPageLoad(origin: String = "bta-sa"): Action[AnyContent] = (authenticate andThen serviceInfo) { implicit request =>
    Ok(enterSAUTR(appConfig, form, origin)(request.serviceInfoContent))
  }

  def onSubmit(origin: String): Action[AnyContent] = (authenticate andThen serviceInfo).async { implicit request =>
    form.bindFromRequest()
      .fold(
        formWithErrors => Future(BadRequest(enterSAUTR(appConfig, formWithErrors, origin)(request.serviceInfoContent))),
        saUTR => {
          lazy val tryAgainBoolean: Future[Boolean] = {
            for {
              tryAgain <- dataCacheConnector.getEntry[Boolean](request.request.credId, "tryAgain").map(_.getOrElse(false))
            } yield {
              tryAgain
            }
          }

          dataCacheConnector.save[SAUTR](request.request.credId, EnterSAUTRId.toString, saUTR).flatMap { _ =>
            tryAgainBoolean.flatMap { tryAgain =>
              if (tryAgain) {
                selectSaCategoryService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, origin)
              } else {
                Future.successful(Redirect(controllers.sa.routes.SelectSACategoryController.onPageLoadHasUTR(Some(origin))))
              }
            }
          }
        }
      )
  }
}
