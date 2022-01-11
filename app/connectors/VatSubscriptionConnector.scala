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
import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.http.Status._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.UpstreamErrorResponse.Upstream4xxResponse
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatSubscriptionConnector @Inject()(val http: HttpClient, val appConfig: FrontendAppConfig) extends Logging {

  lazy val vatSubscriptionUrl: String = appConfig.vatSubscriptionUrl

  def getMandationStatus(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Int] = {
    val url = vatSubscriptionUrl + s"/vat-subscription/$vrn/mandation-status"
    http.GET[HttpResponse](url).map(_.status)
  }.recover {
    case Upstream4xxResponse(error) if error.statusCode == NOT_FOUND => NOT_FOUND
    case Upstream4xxResponse(error) if error.statusCode == PRECONDITION_FAILED => PRECONDITION_FAILED
    case e: Exception =>
      logger.error(s"[VatSubscriptionConnector][getMandationStatus] Failed with error: ${e.getMessage}")
      INTERNAL_SERVER_ERROR
  }
}
