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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import models.sa.JourneyLinkResponse
import play.api.{Logger, Logging}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HttpResponse

class IvConnector @Inject()(appConfig: FrontendAppConfig,
                            http: HttpClient) extends Logging {

  val serviceUrl: String = appConfig.identityVerificationFrontendBaseUrl
  val sttpServiceUrl: String = appConfig.ssttpPostUrl

  def checkJourneyLink(journeyLinkUrl: String)
                      (implicit ec: ExecutionContext, hc: HeaderCarrier): Future[JourneyLinkResponse] = {
    http.GET[JourneyLinkResponse](s"$serviceUrl$journeyLinkUrl")
  }

  def ssttpJourney()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Boolean] = {
    http.POSTEmpty[HttpResponse](s"$sttpServiceUrl")
      .map(_ => true)
      .recover {
        case _ =>
          logger.warn("[IvConnector][ssttpJourney] Failed to update SSTTP with IV success")
          false
      }
    }
}
