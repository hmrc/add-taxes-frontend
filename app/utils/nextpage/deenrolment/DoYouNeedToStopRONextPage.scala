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
import identifiers.DoYouNeedToStopROId
import play.api.mvc.{Call, Request}
import models.deenrolment.DoYouNeedToStopRO
import utils.{Enrolments, NextPage}

trait DoYouNeedToStopRONextPage {

  implicit val doYouNeedToStopRO: NextPage[DoYouNeedToStopROId.type, DoYouNeedToStopRO, Call] = {
    new NextPage[DoYouNeedToStopROId.type, DoYouNeedToStopRO, Call] {
      override def get(b: DoYouNeedToStopRO)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case DoYouNeedToStopRO.Yes => Call("GET", appConfig.emacDeenrolmentsUrl(Enrolments.RebatedOils))
          case DoYouNeedToStopRO.No  => Call("GET", appConfig.getGovUKUrl("rebatedOilsRemove"))
        }
    }
  }
}
