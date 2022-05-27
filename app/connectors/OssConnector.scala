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
import models.vat.{OssRecievedDetails, OssRequestDetails}
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReadsInstances}

import scala.concurrent.{ExecutionContext, Future}

class OssConnector  @Inject()(val http: HttpClient,
                              appConfig: FrontendAppConfig)(implicit ec: ExecutionContext)
  extends Logging with HttpReadsInstances {

  val serviceUrl: String = appConfig.vatOssHost + "/external-entry"

  def ossRegistrationJourneyLink()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[OssRecievedDetails] = {
    val origin = "BTA"
    val returnUrl = "/business-account"
    val ossRequestData = OssRequestDetails(origin = origin, returnUrl = returnUrl)

    http.POST[OssRequestDetails, OssRecievedDetails](serviceUrl, ossRequestData) recover {
      case e => logger.error(s"[OssConnector][ossRegistrationJourneyLink] OSS error message ${e.getMessage}")
        OssRecievedDetails(None)
    }
  }

}
