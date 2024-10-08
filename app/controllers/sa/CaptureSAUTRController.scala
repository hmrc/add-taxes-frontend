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

package controllers.sa

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import forms.sa.CaptureSAUTRFormProvider
import identifiers.{DoYouHaveSAUTRId, EnterSAUTRId}
import models.sa.{DoYouHaveSAUTR, HaveSAUTRModel, SAUTR, SelectSACategory, CaptureSAUTRModel}
import play.api.data.Form
import play.api.data.FormError
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import service.SelectSaCategoryService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.sa.captureSAUTR

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CaptureSAUTRController @Inject()(appConfig: FrontendAppConfig,
                                       mcc: MessagesControllerComponents,
                                       navigator: Navigator[Call],
                                       authenticate: AuthAction,
                                       serviceInfoData: ServiceInfoAction,
                                       captureFormProvider: CaptureSAUTRFormProvider,
                                       captureSAUTR: captureSAUTR,
                                       dataCacheConnector: DataCacheConnector,
                                       selectSaCategoryService: SelectSaCategoryService)(implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[CaptureSAUTRModel] = captureFormProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(captureSAUTR(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    dataCacheConnector.remove(request.request.credId,"tryAgain")
    form.bindFromRequest()
      .fold(
        formWithErrors => {
          BadRequest(captureSAUTR(appConfig, formWithErrors)(request.serviceInfoContent))
        },
        value => {
          if(value.value == DoYouHaveSAUTR.Yes) {
            val saUTR = new SAUTR(value.sautrValue.get)

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
                  selectSaCategoryService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, "bta-sa")
                } else {
                  Future.successful(Redirect(controllers.sa.routes.SelectSACategoryController.onPageLoadHasUTR(Some("bta-sa"))))
                }
              }
            }

            val value2: DoYouHaveSAUTR = DoYouHaveSAUTR.Yes
            Redirect(navigator.nextPage(DoYouHaveSAUTRId, value2))
          } else {
            val value3: DoYouHaveSAUTR = DoYouHaveSAUTR.No
            Redirect(navigator.nextPage(DoYouHaveSAUTRId, value3))
          }
        }
      )
  }
}
