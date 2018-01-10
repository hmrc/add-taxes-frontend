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

import javax.inject.{Inject, Singleton}

import config.FrontendAppConfig
import play.api.Logger
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.play.partials.HtmlPartial._
import uk.gov.hmrc.play.partials.{HeaderCarrierForPartials, HtmlPartial}

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ServiceInfoPartialConnector @Inject()(val http: HttpClient,
                                            val config: FrontendAppConfig) {

  lazy val btaUrl: String = config.btaUrl + "/business-account/partial/service-info"

  def getServiceInfoPartial()(implicit hcwc: HeaderCarrierForPartials): Future[Html] = {
    implicit val executionContext: ExecutionContext = fromLoggingDetails(hcwc.hc)
    http.GET[HtmlPartial](s"$btaUrl")(hc = hcwc.toHeaderCarrier, rds = readsPartial, ec = executionContext) recover connectionExceptionsAsHtmlPartialFailure map { p =>
      p.successfulContentOrEmpty
    } recover {
      case _ =>
        Logger.warn(s"[ServiceInfoPartialConnector][getServiceInfoPartial] - Unexpected future failed error")
        Html("")
    }
  }
}
