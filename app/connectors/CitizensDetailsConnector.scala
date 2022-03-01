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
import models.DesignatoryDetails
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReadsInstances, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class CitizensDetailsConnector @Inject()(val http: HttpClient,
                                         appConfig: FrontendAppConfig)(implicit ec: ExecutionContext)
  extends Logging with HttpReadsInstances {


  def url(identifier: String, value: String): String = appConfig.designatoryDetailsUrl(identifier, value)

  def getDesignatoryDetails(identifier: String, value: String)(implicit hc: HeaderCarrier): Future[Option[DesignatoryDetails]] = {
    http.GET[DesignatoryDetails](url(identifier, value)).map(
      Some(_)
    ).recover {
      case UpstreamErrorResponse(_, 404, _, _) =>
        logger.warn(s"Warn: Not Found returned from Citizen Details")
        None
      case e: Exception =>
        logger.error(s"Error: ${e.getMessage} returned from Citizen Details")
        None
    }
  }

}