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
import forms.sa.{DoYouHaveSAUTRFormProvider, SAUTRFormProvider}
import identifiers.{DoYouHaveSAUTRId, EnterSAUTRId}

import javax.inject.Inject
import models.sa.{DoYouHaveSAUTR, SAUTR, SelectSACategory}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import service.SelectSaCategoryService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Enumerable, Navigator}
import views.html.sa.doYouHaveSAUTR

import scala.concurrent.{ExecutionContext, Future}

class DoYouHaveSAUTRController @Inject()(appConfig: FrontendAppConfig,
                                         mcc: MessagesControllerComponents,
                                         navigator: Navigator[Call],
                                         authenticate: AuthAction,
                                         serviceInfoData: ServiceInfoAction,
                                         formProvider: DoYouHaveSAUTRFormProvider,
                                         doYouHaveSAUTR: doYouHaveSAUTR,
                                         dataCacheConnector: DataCacheConnector,
                                         selectSaCategoryService: SelectSaCategoryService)(implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[DoYouHaveSAUTR] = formProvider()

  def onPageLoad(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(doYouHaveSAUTR(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen serviceInfoData) { implicit request =>
    dataCacheConnector.remove(request.request.credId,"tryAgain")
    form.bindFromRequest()
      .fold(
        formWithErrors => BadRequest(doYouHaveSAUTR(appConfig, formWithErrors)(request.serviceInfoContent)),
        value => {
          val saUTR =  new SAUTR(request.body.asFormUrlEncoded.get("txtdoYouHaveSAUTR")(0))
          println(saUTR.value)
          println(value)

          lazy val tryAgainBoolean: Future[Boolean] = {
            for {
              tryAgain <- dataCacheConnector.getEntry[Boolean](request.request.credId, "tryAgain").map(_.getOrElse(false))
            } yield {
              tryAgain
            }
          }


          println(tryAgainBoolean)

          dataCacheConnector.save[SAUTR](request.request.credId, EnterSAUTRId.toString, saUTR).flatMap { _ =>
            tryAgainBoolean.flatMap { tryAgain =>
              if (tryAgain) {
                selectSaCategoryService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, "bta-sa")
              } else {
                Future.successful(Redirect(controllers.sa.routes.SelectSACategoryController.onPageLoadHasUTR(Some("bta-sa"))))
              }
            }
          }
          dataCacheConnector.save[SAUTR](request.request.credId, EnterSAUTRId.toString, saUTR)

          val utr = dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
          println("print utr")
          println(utr.value)
          Redirect(navigator.nextPage(DoYouHaveSAUTRId, value))
        }
      )
  }
}
