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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import models.sa.{KnownFacts, KnownFactsAndIdentifiers, SAUTR, SaEnrolment}
import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentStoreProxyConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClient) {

  def checkExistingUTR(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] =
    http.GET[HttpResponse](appConfig.checkUtrUrl(utr)).map { response =>
      response.status match {
        case OK         => true
        case NO_CONTENT => false
        case status =>
          Logger.error(s"Enrolment Store Proxy returned status code: $status, body: ${response.body}")
          false
      }
    } recover {
      case exception =>
        Logger.error("Enrolment Store Proxy error", exception)
        false
    }

  def checkExistingEmpRef(officeNumber: String, payeReference: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] =
    http.GET[HttpResponse](appConfig.checkEmpRefUrl(officeNumber, payeReference)).map { response =>
      response.status match {
        case OK         => true
        case NO_CONTENT => false
        case status =>
          Logger.error(s"Enrolment Store Proxy returned status code: $status, body: ${response.body}")
          false
      }
    } recover {
      case exception =>
        Logger.error("Enrolment Store Proxy error", exception)
        false
    }


  lazy val serviceUrl = s"${appConfig.enrolmentStoreHost}/enrolment-store-proxy/enrolment-store"

  def queryKnownFacts(utr: SAUTR, knownFacts: KnownFacts)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    val knownFactsCombined = KnownFactsAndIdentifiers(utr.value, knownFacts.nino, knownFacts.postcode)

    http.POST[KnownFactsAndIdentifiers, HttpResponse](s"$serviceUrl/enrolments", knownFactsCombined).map {
        _.status == OK
    }.recover {
      case exception =>
        Logger.error("Enrolment Store Proxy error for queryKnownFacts", exception)
        false
    }
  }

  def enrolForSa(saEnrolment: SaEnrolment, utr: String, groupId: String)(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse]= {
    http.POST[SaEnrolment, HttpResponse](s"/enrolment-store/groups/${groupId}/enrolments/IR-SA~UTR~${utr}", saEnrolment)
  }
}
