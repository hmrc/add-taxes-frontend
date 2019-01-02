/*
 * Copyright 2019 HM Revenue & Customs
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
import models.MandationStatus
import play.api.Logger
import play.api.http.Status.{NOT_FOUND, OK}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatSubscriptionConnector @Inject()(val http: HttpClient, val appConfig: FrontendAppConfig) {

  lazy val vatSubscriptionUrl: String = appConfig.vatSubscriptionUrl

  private def handleResponse[A](uri: String)(implicit rds: HttpReads[A]): HttpReads[Either[String, Option[A]]] =
    new HttpReads[Either[String, Option[A]]] {
      override def read(method: String, url: String, response: HttpResponse): Either[String, Option[A]] =
        response.status match {
          case OK =>
            Logger.debug(
              "[VatSubscriptionConnector][handleResponse.read] - Successfully retrieved OK 200 response with body:"
                + response.body)
            Right(Some(rds.read(method, url, response)))
          case NOT_FOUND =>
            Logger.debug("[VatSubscriptionConnector][handleResponse.read] - Received 404 when getting mandation status")
            Right(None)
          case _ =>
            Logger.warn(
              s"[VatSubscriptionConnector][handleResponse.read] - Failed to retrieve mandation status. Received status: ${response.status}." +
                s"Response body: ${response.body}")
            Left("Failed")
        }
    }

  def getMandationStatus(
    vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[String, Option[MandationStatus]]] = {
    val url = vatSubscriptionUrl + s"/vat-subscription/$vrn/mandation-status"
    http.GET[Either[String, Option[MandationStatus]]](url)(handleResponse[MandationStatus](url), hc, ec)
  }
}
