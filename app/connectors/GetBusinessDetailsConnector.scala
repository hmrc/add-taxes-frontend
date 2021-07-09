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
import controllers.Assets.OK

import javax.inject.Inject
import models.BusinessDetails
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class GetBusinessDetailsConnector @Inject()(val http: HttpClient, appConfig: FrontendAppConfig) extends Logging{


  def serviceUrl(identifier: String, value: String) = {
      appConfig.businessDetailsUrl(identifier, value)
  }

  def getBusinessDetails(identifier: String, value: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[BusinessDetails]] = {
    //TODO we need http headers Environment and Bearer Token passed in I think? we need to check
    http.GET[BusinessDetails](serviceUrl(identifier, value)).map(
      Some(_)
    ).recover {
      case e: Exception =>
        logger.error(s"[GetBusinessDetailsConnector][getBusinessDetails]Error: ${e.getMessage} returned from Citizen Details")
        None
    }
  }
}


