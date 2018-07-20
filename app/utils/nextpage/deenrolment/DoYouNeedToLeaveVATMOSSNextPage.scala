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

package utils.nextpage.deenrolment

import config.FrontendAppConfig
import identifiers.DoYouNeedToLeaveVATMOSSId
import play.api.mvc.{Call, Request}
import models.deenrolment.DoYouNeedToLeaveVATMOSS
import utils.{Enrolments, NextPage}

trait DoYouNeedToLeaveVATMOSSNextPage {

  implicit val doYouNeedToLeaveVATMOSS: NextPage[DoYouNeedToLeaveVATMOSSId.type, DoYouNeedToLeaveVATMOSS] = {
    new NextPage[DoYouNeedToLeaveVATMOSSId.type, DoYouNeedToLeaveVATMOSS] {
      override def get(b: DoYouNeedToLeaveVATMOSS)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouNeedToLeaveVATMOSS.Yes => Call("GET", appConfig.getPortalUrl("mossChangeDetails"))
          case DoYouNeedToLeaveVATMOSS.No  => Call("GET", appConfig.emacDeenrolmentsUrl(Enrolments.VATMOSS))
        }
    }
  }
}
