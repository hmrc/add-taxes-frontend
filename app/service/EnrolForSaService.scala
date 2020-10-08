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

package service

import config.FrontendAppConfig
import connectors.EnrolmentStoreProxyConnector
import controllers.Assets.CREATED
import javax.inject.Inject
import models.sa.SaEnrolment
import play.api.Logger
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class EnrolForSaService @Inject()(enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector) {

  def enrolForSa(utr: String, credId: String, groupId: String)
                (implicit ec: ExecutionContext, request: Request[_], hc: HeaderCarrier): Future[Boolean] = {
    val saEnrolment: SaEnrolment = new SaEnrolment(credId)
    enrolmentStoreProxyConnector.enrolForSa(saEnrolment, utr, groupId).flatMap { response =>
      response.status match {
        case CREATED => Future.successful(true)
      }
    }.recover {
      case e: Throwable =>
        Logger.error(s"[EnrolForSaController][enrolForSa] failed with error ${e.getMessage}")
        false
    }
  }
}
