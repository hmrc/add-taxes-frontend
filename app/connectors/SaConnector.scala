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
import models.sa.IvLinks
import play.api.Logger
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import scala.concurrent.{ExecutionContext, Future}

class SaConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClient) {

  def serviceUrl(utr: String) = s"${appConfig.saBaseUrl}/sa/individual/${utr}/details-for-iv"

  def getIvLinks(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[IvLinks]] = {
    http.GET[IvLinks](serviceUrl(utr)).map { result =>
      Some(result)
    }.recover {
      case exception =>
        Logger.error("Enrolment Store Proxy error", exception)
        None
    }
  }
}
