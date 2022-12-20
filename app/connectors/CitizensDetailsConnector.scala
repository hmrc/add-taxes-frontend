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

import javax.inject.Inject
import models.{DesignatoryDetails, DesignatoryDetailsForKnownFacts}
import models.requests.ServiceInfoRequest
import play.api.http.Status.NOT_FOUND
import play.api.mvc.AnyContent
import utils.LoggingUtil
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReadsInstances, NotFoundException, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class CitizensDetailsConnector @Inject()(val http: HttpClient,
                                         appConfig: FrontendAppConfig)(implicit ec: ExecutionContext)
  extends LoggingUtil with HttpReadsInstances {


  def url(identifier: String, value: String): String = appConfig.designatoryDetailsUrl(identifier, value)

  def getDesignatoryDetails(identifier: String, value: String)
                           (implicit hc: HeaderCarrier, request: ServiceInfoRequest[AnyContent]): Future[Option[DesignatoryDetails]] = {
    http.GET[DesignatoryDetails](url(identifier, value)).map(
      Some(_)
    ).recover {
      case UpstreamErrorResponse(_, 404, _, _) =>
        warnLog(s"Warn: Not Found returned from Citizen Details")
        None
      case e: Exception =>
        errorLog(s"Error: ${e.getMessage} returned from Citizen Details")
        None
    }
  }

  def getDesignatoryDetailsForKnownFacts(identifier: String, value: String)
                           (implicit hc: HeaderCarrier, request: ServiceInfoRequest[AnyContent]): Future[Option[DesignatoryDetailsForKnownFacts]] = {
    http.GET[DesignatoryDetailsForKnownFacts](url(identifier, value)).map(
      Some(_)
    ).recover {
      case UpstreamErrorResponse(_, NOT_FOUND, _, _) =>
        warnLog(s"Warn: Not Found returned from Citizen Details service")
        throw new NotFoundException("[CitizensDetailsConnector][getDesignatoryDetails] Not Found returned from Citizen Details")
      case e: Exception =>
        errorLog(s"Error: ${e.getMessage} returned from Citizen Details")
        None
    }
  }

}