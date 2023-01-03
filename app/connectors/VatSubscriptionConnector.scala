/*
 * Copyright 2023 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import utils.LoggingUtil
import play.api.http.Status._
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.UpstreamErrorResponse.Upstream4xxResponse
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatSubscriptionConnector @Inject()(val http: HttpClient, val appConfig: FrontendAppConfig) extends LoggingUtil {

  lazy val vatSubscriptionUrl: String = appConfig.vatSubscriptionUrl

  def getMandationStatus(vrn: String)
                        (implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[Int] = {
    val url = vatSubscriptionUrl + s"/vat-subscription/$vrn/mandation-status"
    http.GET[HttpResponse](url).map(_.status)
  }.recover {
    case Upstream4xxResponse(error) if error.statusCode == NOT_FOUND => NOT_FOUND
    case Upstream4xxResponse(error) if error.statusCode == PRECONDITION_FAILED => PRECONDITION_FAILED
    case e: Exception =>
      errorLog(s"[VatSubscriptionConnector][getMandationStatus] Failed with error: ${e.getMessage}")
      INTERNAL_SERVER_ERROR
  }
}
