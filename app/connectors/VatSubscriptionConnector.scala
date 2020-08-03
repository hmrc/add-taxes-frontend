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
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatSubscriptionConnector @Inject()(val http: HttpClient, val appConfig: FrontendAppConfig) {

  lazy val vatSubscriptionUrl: String = appConfig.vatSubscriptionUrl

  private def handleResponse()(implicit rds: HttpReads[Boolean]): HttpReads[Int] =
    new HttpReads[Int] {
      override def read(method: String, url: String, response: HttpResponse): Int = response.status
    }

  def getMandationStatus(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Int] = {
    val url = vatSubscriptionUrl + s"/vat-subscription/$vrn/mandation-status"
    http.GET[Int](url)(handleResponse(), hc, ec)
  }
}
