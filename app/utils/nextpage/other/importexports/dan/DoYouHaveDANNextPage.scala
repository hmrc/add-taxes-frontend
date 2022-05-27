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

package utils.nextpage.other.importexports.dan

import config.FrontendAppConfig
import controllers.other.importexports.dan.{routes => danRoutes}
import identifiers.DoYouHaveDANId
import models.other.importexports.dan.DoYouHaveDAN
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}

trait DoYouHaveDANNextPage {

  implicit val doYouHaveDAN: NextPage[DoYouHaveDANId.type, DoYouHaveDAN, Call] = {
    new NextPage[DoYouHaveDANId.type, DoYouHaveDAN, Call] {
      override def get(b: DoYouHaveDAN)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case DoYouHaveDAN.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.DefermentApprovalNumber))
          case DoYouHaveDAN.No  => danRoutes.RegisterDefermentApprovalNumberController.onPageLoad()
        }
    }
  }
}
