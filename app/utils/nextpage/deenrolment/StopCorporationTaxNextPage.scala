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

package utils.nextpage.deenrolment

import config.FrontendAppConfig
import identifiers.StopCorporationTaxId
import models.deenrolment.StopCorporationTax
import models.requests.ServiceInfoRequest
import play.api.mvc.Call

import utils.Enrolments.CT
import utils.NextPage

trait StopCorporationTaxNextPage {

  implicit val stopCorporationTax: NextPage[StopCorporationTaxId.type, StopCorporationTax, Call] = {
    new NextPage[StopCorporationTaxId.type, StopCorporationTax, Call] {
      override def get(b: StopCorporationTax)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case StopCorporationTax.Stop    => Call("GET", appConfig.emacDeenrolmentsUrl(CT))
          case StopCorporationTax.Dormant => Call("GET", appConfig.getGovUKUrl("dormant-ct"))
          case StopCorporationTax.Close   => Call("GET", appConfig.getGovUKUrl("closing-company"))
        }
    }
  }
}
