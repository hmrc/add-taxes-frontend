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

package utils.nextpage.deenrolment

import config.FrontendAppConfig
import identifiers.DoYouNeedToCancelVATId
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import models.deenrolment.DoYouNeedToCancelVAT
import utils.{Enrolments, NextPage}

trait DoYouNeedToCancelVATNextPage {

  implicit val doYouNeedToCancelVAT: NextPage[DoYouNeedToCancelVATId.type, DoYouNeedToCancelVAT, Call] = {
    new NextPage[DoYouNeedToCancelVATId.type, DoYouNeedToCancelVAT, Call] {
      override def get(b: DoYouNeedToCancelVAT)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case DoYouNeedToCancelVAT.Yes => Call("GET", appConfig.getBusinessAccountUrl("vat-deregister"))
          case DoYouNeedToCancelVAT.No  => Call("GET", appConfig.emacDeenrolmentsUrl(Enrolments.VAT))
        }
    }
  }
}
