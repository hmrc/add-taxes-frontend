/*
 * Copyright 2018 HM Revenue & Customs
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

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enumerable, HmrcEnrolmentType, Navigator, RadioOption}
import forms.sa.SelectSACategoryFormProvider
import identifiers.SelectSACategoryId
import models.sa.SelectSACategory
import uk.gov.hmrc.auth.core.Enrolments
import views.html.sa.selectSACategory
import utils.&&

class SelectSACategoryController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: SelectSACategoryFormProvider)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad() = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(selectSACategory(appConfig, form, getRadioOptions(request.request.enrolments))(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData) { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          BadRequest(
            selectSACategory(appConfig, formWithErrors, getRadioOptions(request.request.enrolments))(
              request.serviceInfoContent)),
        (value) => Redirect(navigator.nextPage(SelectSACategoryId, (value, request.request.affinityGroup)))
      )
  }

  def getRadioOptions(enrolments: Enrolments): Set[RadioOption] =
    enrolments match {
      case HmrcEnrolmentType.SA() && HmrcEnrolmentType.RegisterTrusts() =>
        SelectSACategory.options.filter(_.value == SelectSACategory.Partnership.toString)

      case HmrcEnrolmentType.SA() =>
        SelectSACategory.options.filterNot(_.value == SelectSACategory.Sa.toString)

      case HmrcEnrolmentType.RegisterTrusts() =>
        SelectSACategory.options.filterNot(_.value == SelectSACategory.Trust.toString)

      case _ => SelectSACategory.options
    }
}
