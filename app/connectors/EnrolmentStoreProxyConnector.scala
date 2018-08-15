/*
 * Copyright 2018 HM Revenue & Customs
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
import javax.inject.Inject

import com.google.inject.ImplementedBy
import config.FrontendAppConfig
import play.api.{Logger, LoggerLike}
import play.api.http.Status._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentStoreProxyConnectorImpl @Inject()(appConfig: FrontendAppConfig, http: HttpClient)
    extends EnrolmentStoreProxyConnector {

  def checkExistingUTR(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] =
    http.GET(appConfig.checkUtrUrl(utr)).map { response =>
      response.status match {
        case OK         => true
        case NO_CONTENT => false
        case status =>
          Logger.error(s"Enrolment Store Proxy returned status code: $status, body: ${response.body}")
          false
      }
    } recover {
      case exception =>
        Logger.error("Enrolment Store Proxy error", exception)
        false
    }
}

@ImplementedBy(classOf[EnrolmentStoreProxyConnectorImpl])
trait EnrolmentStoreProxyConnector {

  def checkExistingUTR(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean]
}
