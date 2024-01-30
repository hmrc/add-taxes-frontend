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

package utils.nextpage.employer.cis.uk.subcontractor

import config.FrontendAppConfig
import identifiers.DoYouWantToBePaidNetOrGrossId
import models.employer.cis.uk.subcontractor.DoYouWantToBePaidNetOrGross
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import utils.NextPage

trait DoYouWantToBePaidNetOrGrossNextPage {

  implicit val doYouWantToBePaidNetOrGross
    : NextPage[DoYouWantToBePaidNetOrGrossId.type, DoYouWantToBePaidNetOrGross, Call] = {
    new NextPage[DoYouWantToBePaidNetOrGrossId.type, DoYouWantToBePaidNetOrGross, Call] {
      override def get(b: DoYouWantToBePaidNetOrGross)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case DoYouWantToBePaidNetOrGross.PaidNet => Call("GET", appConfig.getGovUKUrl("cisRegisterPaidNet"))
          case DoYouWantToBePaidNetOrGross.PaidGross =>
            Call("GET", appConfig.getGovUKUrl("cisRegisterPaidGrossOrLowTurnOver"))
        }
    }
  }
}
