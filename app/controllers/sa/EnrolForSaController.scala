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
import connectors.EnrolmentStoreProxyConnector
import controllers.actions.{AuthAction, ServiceInfoAction}
import javax.inject.Inject
import models.sa.SaEnrolment
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Enumerable
import views.html.sa.knownFacts

import scala.concurrent.{ExecutionContext, Future}



class EnrolForSaController@Inject()(enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector,
                                    authenticate: AuthAction,
                                    serviceInfoData: ServiceInfoAction,
                                    mcc: MessagesControllerComponents,
                                    appConfig: FrontendAppConfig)
  extends FrontendController(mcc) with I18nSupport with Enumerable.Implicits {

  def enrolForSa(utr: String, credId: String, groupId: String)(implicit ec: ExecutionContext): Action[AnyContent]
  = (authenticate andThen serviceInfoData).async {
    implicit request =>
      val saEnrolment: SaEnrolment = new SaEnrolment(credId)
      enrolmentStoreProxyConnector.enrolForSa(saEnrolment, utr, groupId).flatMap { response =>
        response.status match {
          case CREATED =>
            //location TBC
          Future.successful(SeeOther(appConfig.getBusinessAccountUrl("home")))
          case BAD_REQUEST =>
            //location TBC
          Logger.error("[EnrolForSaController][enrolForSa] Bad request from enrolment store proxy")
            Future.successful(SeeOther(appConfig.getBusinessAccountUrl("wrong-credentials")))
          case CONFLICT =>
            //location TBC
          Logger.error("[EnrolForSaController][enrolForSa] CONFLICT from enrolment store proxy")
            Future.successful(SeeOther(appConfig.getBusinessAccountUrl("wrong-credentials")))
          case FORBIDDEN =>
            //location TBC
          Logger.error("[EnrolForSaController][enrolForSa] FORBIDDEN from enrolment store proxy")
            Future.successful(SeeOther(appConfig.getBusinessAccountUrl("wrong-credentials")))
          case NOT_FOUND =>
            //location TBC
            Logger.error("[EnrolForSaController][enrolForSa] NOT_FOUND from enrolment store proxy")
            Future.successful(SeeOther(appConfig.getBusinessAccountUrl("wrong-credentials")))
        }
      }.recover {
        case e: Throwable =>
          //location TBC
        Logger.error(s"[EnrolForSaController][enrolForSa] failed with error ${e.getMessage}")
          SeeOther(appConfig.getBusinessAccountUrl("wrong-credentials"))
      }
   }
}
