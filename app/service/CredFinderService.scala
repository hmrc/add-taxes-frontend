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

package service

import connectors.{CitizensDetailsConnector, DataCacheConnector, GetBusinessDetailsConnector}
import identifiers.EnterSAUTRId
import models.requests.ServiceInfoRequest
import models.sa.SAUTR
import play.api.mvc.AnyContent

import javax.inject.Inject
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class CredFinderService @Inject()(citizensDetailsConnector: CitizensDetailsConnector, getBusinessDetailsConnector: GetBusinessDetailsConnector, dataCacheConnector: DataCacheConnector) {

  def utrCheck(enrolments: Option[Set[Enrolment]])(implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[Boolean] = {
    enrolments match {
      case Some(enrolment) => {enrolment.map {
        enrolment =>
          enrolment.key match {
            case "IR-SA" => val utr = enrolment.getIdentifier("utr")
              utr match {
                case Some(value) => mtdITSASignupBool(enrolment.key, value)
                case _ => Future.successful(false)
              }
            case _ => val utr: Option[EnrolmentIdentifier] = dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString).map(_.getOrElse(SAUTR("")))
              utr match {
                case Some(value) => mtdITSASignupBool(enrolment.key, value)
                case _ => Future.successful(false)
              }

          }
      }.head}
      case _ => Future.successful(false)
    }

  }

  def mtdITSASignupBool(key: String, utr: EnrolmentIdentifier)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    citizensDetailsConnector.getDesignatoryDetails(key, utr.value).flatMap {
      case Some(details) => getBusinessDetailsConnector.getBusinessDetails("nino", details.nino).map {
        case Some(_) => true
        case _ => false
      }
      case _ => Future.successful(false)
    }
  }
}

