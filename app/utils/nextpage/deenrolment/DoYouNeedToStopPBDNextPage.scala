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

package utils.nextpage.deenrolment

import config.FrontendAppConfig
import identifiers.DoYouNeedToStopPBDId
import play.api.mvc.{Call, Request}
import models.deenrolment.DoYouNeedToStopPBD
import utils.{Enrolments, NextPage}

trait DoYouNeedToStopPBDNextPage {

  implicit val doYouNeedToStopPBD: NextPage[DoYouNeedToStopPBDId.type, DoYouNeedToStopPBD, Call] = {
    new NextPage[DoYouNeedToStopPBDId.type, DoYouNeedToStopPBD, Call] {
      override def get(b: DoYouNeedToStopPBD)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case DoYouNeedToStopPBD.Yes => Call("GET", appConfig.emacDeenrolmentsUrl(Enrolments.PoolBetting))
          case DoYouNeedToStopPBD.No  => Call("GET", appConfig.getGovUKUrl("deenrolGambling"))
        }
    }
  }
}
