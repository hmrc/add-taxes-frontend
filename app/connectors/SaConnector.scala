/*
 * Copyright 2024 HM Revenue & Customs
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
import models.BusinessDetails
import models.requests.ServiceInfoRequest
import models.sa.IvLinks
import utils.LoggingUtil
import play.api.http.Status.NOT_FOUND
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class SaConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClient) extends LoggingUtil {

  def serviceUrl(utr: String, origin: String): String = s"${appConfig.saBaseUrl}/sa/individual/$utr/details-for-iv?origin=$origin"
  def serviceBusinessDetails(value: String, identifier: String): String = s"${appConfig.saBaseUrl}/sa/business-details/$value/$identifier"

  def getIvLinks(utr: String, origin: String)
                (implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[Option[IvLinks]] = {
    http.GET[IvLinks](serviceUrl(utr, origin)).map { result =>
      Some(result)
    }.recover {
      case UpstreamErrorResponse(_, NOT_FOUND, _, _) => None
      case exception =>
        errorLog("[SaConnector][getIvLinks] resulted in", exception)
        None
    }
  }


  def getBusinessDetails(value: String, identifier: String)
                        (implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[Option[BusinessDetails]] = {
    http.GET[BusinessDetails](serviceBusinessDetails(value, identifier)).map { result =>
      Some(result)
    }.recover {
      case UpstreamErrorResponse(_, NOT_FOUND, _, _) => None
      case exception =>
        errorLog(s"[SaConnector][getBusinessDetails] resulted in ${exception.getMessage}")
        None
    }
  }
}
