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
import models.vat.{OssRecievedDetails, OssRequestDetails}
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReadsInstances}
import utils.LoggingUtil

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OssConnector  @Inject()(val http: HttpClient,
                              appConfig: FrontendAppConfig)(implicit ec: ExecutionContext)
  extends LoggingUtil with HttpReadsInstances {

  val serviceUrl: String = appConfig.vatOssExternalEntry

  def ossRegistrationJourneyLink()(implicit hc: HeaderCarrier,
                                   ec: ExecutionContext,
                                   request: ServiceInfoRequest[AnyContent]): Future[OssRecievedDetails] = {
    val origin = "BTA"
    val returnUrl = "/business-account"
    val ossRequestData = OssRequestDetails(origin = origin, returnUrl = returnUrl)
    infoLog(s"[OssConnector][ossRegistrationJourneyLink] attempted with: $origin, $returnUrl")
    http.POST[OssRequestDetails, OssRecievedDetails](serviceUrl, ossRequestData) recover {
      case e => errorLog(s"[OssConnector][ossRegistrationJourneyLink] OSS error message ${e.getMessage}")
        OssRecievedDetails(None)
    }
  }

}
