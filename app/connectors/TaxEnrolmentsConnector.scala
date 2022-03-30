/*
 * Copyright 2022 HM Revenue & Customs
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
import models.requests.ServiceInfoRequest

import javax.inject.Inject
import models.sa.SaEnrolment
import play.api.Logging
import play.api.http.Status.{CONFLICT, CREATED}
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.UpstreamErrorResponse.Upstream4xxResponse
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

class TaxEnrolmentsConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClient) extends Logging {

  val serviceUrl: String = appConfig.enrolForSaUrl

  def enrolForSa(utr: String, action: String)
                (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[Boolean]= {

    val saEnrolment: SaEnrolment = new SaEnrolment(request.request.credId, action)
    http.POST[SaEnrolment, HttpResponse](s"$serviceUrl${request.request.groupId}/enrolments/IR-SA~UTR~$utr", saEnrolment).map { response =>
      response.status match {
        case CREATED => true
        case CONFLICT if request.request.enrolments.getEnrolment("IR-SA").isDefined =>
          logger.info(s"[TaxEnrolmentsConnector][enrolForSa] Enrolment already created. Likely double click")
          true
        case _ =>
          logger.error(s"[EnrolForSaController][enrolForSa] failed with status ${response.status}, body: ${response.body}")
          false
      }
    }.recover {
      case Upstream4xxResponse(error) =>
        logger.error(s"[TaxEnrolmentsConnector][enrolForSa] Enrolment Store Proxy status ${error.statusCode}, message ${error.message}")
        false
      case exception =>
        logger.error("[TaxEnrolmentsConnector][enrolForSa]Enrolment Store Proxy error", exception)
        false
    }
  }
}
