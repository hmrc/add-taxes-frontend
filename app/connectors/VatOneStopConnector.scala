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
import models.requests.ServiceInfoRequest
import models.vat.{VatOneStopRecievedDetails, VatOneStopRequestDetails, WhichVATServicesToAdd}
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReadsInstances}
import utils.LoggingUtil

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VatOneStopConnector @Inject()(val http: HttpClient,
                              appConfig: FrontendAppConfig)
  extends LoggingUtil with HttpReadsInstances {

  val serviceOssUrl: String = appConfig.vatOssExternalEntry
  val serviceIossUrl: String = appConfig.vatIossExternalEntry

  def vatOneStopRegistrationJourneyLink(urlChoice: WhichVATServicesToAdd,
                                        langKey: String)
                                       (implicit hc: HeaderCarrier,
                                       ec: ExecutionContext,
                                       request: ServiceInfoRequest[AnyContent]): Future[VatOneStopRecievedDetails] = {
    val origin = "BTA"
    val returnUrl = "/business-account"
    val ossRequestData = VatOneStopRequestDetails(origin = origin, returnUrl = returnUrl)
    val urlLang = s"?lang=$langKey"
    val serviceUrl = urlChoice match{
      case WhichVATServicesToAdd.VATOSS => serviceOssUrl + urlLang
      case WhichVATServicesToAdd.VATIOSS => serviceIossUrl + urlLang
      case _ => serviceOssUrl + urlLang
    }

    infoLog(s"[VatOneStopConnector][vatOneStopRegistrationJourneyLink] attempted with: $origin, $returnUrl and $urlChoice")
    http.POST[VatOneStopRequestDetails, VatOneStopRecievedDetails](serviceUrl, ossRequestData) recover {
      case e => errorLog(s"[VatOneStopConnector][vatOneStopRegistrationJourneyLink] OSS error message ${e.getMessage}")
        VatOneStopRecievedDetails(None)
    }
  }

}
