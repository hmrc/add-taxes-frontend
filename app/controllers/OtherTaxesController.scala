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

package controllers

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import forms.OtherTaxesFormProvider
import identifiers.OtherTaxesId
import models.OtherTaxes.AlcoholAndTobacco
import models.requests.ServiceInfoRequest
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.AnyContent
import uk.gov.hmrc.auth.core.Enrolment
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enrolments, Enumerable, Navigator, RadioOption}
import views.html.otherTaxes

import scala.concurrent.Future

class OtherTaxesController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        authenticate: AuthAction,
                                        serviceInfoData: ServiceInfoAction,
                                        formProvider: OtherTaxesFormProvider) extends FrontendController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def getOptions(implicit r: ServiceInfoRequest[AnyContent]): Seq[RadioOption] = {
    val checks = Seq(checkAlcohol)
    checks.flatMap(_.apply(r.request.enrolments))
  }

  private def checkAlcohol: (uk.gov.hmrc.auth.core.Enrolments) => Option[RadioOption] =
    (enrolments: uk.gov.hmrc.auth.core.Enrolments) =>
    if(checkAnnualTaxOnEnvelopedDwellings(enrolments) && checkAlcoholAndTobaccoWarehousingDeclarations(enrolments)) {
      None
    } else {
      Some(AlcoholAndTobacco.toRadioOption)
    }

  private def checkAnnualTaxOnEnvelopedDwellings: (uk.gov.hmrc.auth.core.Enrolments) => Boolean =
    _.getEnrolment(Enrolments.AnnualTaxOnEnvelopedDwellings.toString).isDefined

  private def checkAlcoholAndTobaccoWarehousingDeclarations: (uk.gov.hmrc.auth.core.Enrolments) => Boolean =
    _.getEnrolment(Enrolments.AlcoholAndTobaccoWarehousingDeclarations.toString).isDefined

  def onPageLoad() = (authenticate andThen serviceInfoData) {
    implicit request =>
      Ok(otherTaxes(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(otherTaxes(appConfig, formWithErrors)(request.serviceInfoContent))),
        (value) =>
          Future.successful(Redirect(navigator.nextPage(OtherTaxesId, value)))
      )
  }
}
