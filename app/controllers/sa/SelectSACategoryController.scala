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
import controllers.sa.partnership.routes.DoYouWantToAddPartnerController
import forms.sa.SelectSACategoryFormProvider
import controllers.sa.PostcodeController

import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.{DoYouHaveSAUTR, SelectSACategory}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import service.{CredFinderService, SelectSaCategoryService}
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils._
import views.html.sa.selectSACategory

import scala.concurrent.{ExecutionContext, Future}

class SelectSACategoryController @Inject()(appConfig: FrontendAppConfig,
                                           mcc: MessagesControllerComponents,
                                           authenticate: AuthAction,
                                           serviceInfoData: ServiceInfoAction,
                                           formProvider: SelectSACategoryFormProvider,
                                           selectSACategory: selectSACategory,
                                           selectSaCategoryService: SelectSaCategoryService,
                                           credFinderService: CredFinderService,
                                          )
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  implicit val ec: ExecutionContext = mcc.executionContext

  val form: Form[SelectSACategory] = formProvider()

  private def onPageLoad(action: Call,
                         origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData).async {
    implicit request => credFinderService.redirectSACategory(form, action, origin)
  }

  def onPageLoadHasUTR(origin: Option[String]): Action[AnyContent] = {
    origin match {
      case Some(origin) => onPageLoad(routes.SelectSACategoryController.onSubmitHasUTR(origin), origin)
      case _ => onPageLoad(routes.SelectSACategoryController.onSubmitHasUTR("bta-sa"), "bta-sa")
    }
  }

  def onPageLoadNoUTR(): Action[AnyContent] = onPageLoad(routes.SelectSACategoryController.onSubmitNoUTR(), "bta-sa")

  private def onSubmit(action: Call,
                       answer: DoYouHaveSAUTR,
                       origin: String): Action[AnyContent] = {
    (authenticate andThen serviceInfoData).async { implicit request =>
        form.bindFromRequest()
          .fold(
            formWithErrors =>
              Future(BadRequest(selectSACategory(appConfig, formWithErrors, action, origin, credFinderService.getRadioOptions(request.request.enrolments))(request.serviceInfoContent))),
            value => selectSaCategoryService.saCategoryResult(value, answer, origin)
          )
      }
    }

  def onSubmitHasUTR(origin: String): Action[AnyContent] =
    onSubmit(routes.SelectSACategoryController.onSubmitHasUTR(origin), DoYouHaveSAUTR.Yes, origin)
  def onSubmitNoUTR(): Action[AnyContent] =
    onSubmit(routes.SelectSACategoryController.onSubmitNoUTR(), DoYouHaveSAUTR.No, "bta-sa")

}
