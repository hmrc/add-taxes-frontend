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
import controllers.actions._
import controllers.sa.partnership.routes.DoYouWantToAddPartnerController
import forms.sa.SelectSACategoryFormProvider
import identifiers.SelectSACategoryId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.{DoYouHaveSAUTR, SelectSACategory}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils._
import views.html.sa.selectSACategory

class SelectSACategoryController @Inject()(appConfig: FrontendAppConfig,
                                           mcc: MessagesControllerComponents,
                                           navigator: Navigator[Call],
                                           authenticate: AuthAction,
                                           serviceInfoData: ServiceInfoAction,
                                           formProvider: SelectSACategoryFormProvider,
                                           selectSACategory: selectSACategory)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  val form: Form[SelectSACategory] = formProvider()

  private def onPageLoad(action: Call): Action[AnyContent] = (authenticate andThen serviceInfoData) {
    implicit request =>
      redirectWhenHasSAAndRT {
        Ok(selectSACategory(appConfig, form, action, getRadioOptions(request.request.enrolments))(request.serviceInfoContent))
      }
  }

  def onPageLoadHasUTR: Action[AnyContent] = onPageLoad(routes.SelectSACategoryController.onSubmitHasUTR())
  def onPageLoadNoUTR: Action[AnyContent] = onPageLoad(routes.SelectSACategoryController.onSubmitNoUTR())

  private def onSubmit(action: Call, answer: DoYouHaveSAUTR): Action[AnyContent] = {
    (authenticate andThen serviceInfoData) { implicit request =>
      redirectWhenHasSAAndRT {
        form.bindFromRequest()
          .fold(
            formWithErrors =>
              BadRequest(selectSACategory(appConfig, formWithErrors, action, getRadioOptions(request.request.enrolments))(request.serviceInfoContent)),
            value => Redirect(navigator.nextPage(SelectSACategoryId, (value, answer, request.request.affinityGroup)))
          )
      }
    }
  }

  def onSubmitHasUTR: Action[AnyContent] = onSubmit(routes.SelectSACategoryController.onSubmitHasUTR(), DoYouHaveSAUTR.Yes)
  def onSubmitNoUTR: Action[AnyContent] = onSubmit(routes.SelectSACategoryController.onSubmitNoUTR(), DoYouHaveSAUTR.No)


  private def redirectWhenHasSAAndRT[A](noRedirect: => Result)(implicit request: ServiceInfoRequest[A]): Result = {
    request.request.enrolments match {
      case HmrcEnrolmentType.SA() && HmrcEnrolmentType.RegisterTrusts() =>
        Redirect(DoYouWantToAddPartnerController.onPageLoad())
      case _ =>
        noRedirect
    }
  }

  private def getRadioOptions(enrolments: Enrolments): Set[RadioOption] = {
    enrolments match {
      case HmrcEnrolmentType.SA() => SelectSACategory.options.filterNot(_.value == SelectSACategory.Sa.toString)
      case HmrcEnrolmentType.RegisterTrusts() =>
        SelectSACategory.options.filterNot(_.value == SelectSACategory.Trust.toString)
      case _ => SelectSACategory.options
    }
  }
}
