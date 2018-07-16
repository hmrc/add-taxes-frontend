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

package utils.nextpage.other.ctf

import config.FrontendAppConfig
import identifiers.AreYouApprovedCTFId
import play.api.mvc.{Call, Request}
import models.other.ctf.AreYouApprovedCTF
import utils.{Enrolments, NextPage}
import controllers.other.ctf.{routes => ctfRoutes}

trait AreYouApprovedCTFNextPage {

  implicit val areYouApprovedCTF: NextPage[AreYouApprovedCTFId.type, AreYouApprovedCTF] = {
    new NextPage[AreYouApprovedCTFId.type, AreYouApprovedCTF] {
      override def get(b: AreYouApprovedCTF)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case AreYouApprovedCTF.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.CTF))
          case AreYouApprovedCTF.No  => ctfRoutes.YouNeedToBeApprovedCTFController.onPageLoad()
        }
    }
  }
}
