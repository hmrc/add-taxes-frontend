/*
 * Copyright 2019 HM Revenue & Customs
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

package utils.nextpage.other.gambling.pbd

import config.FrontendAppConfig
import identifiers.DoYouHavePBDRegistrationId
import play.api.mvc.{Call, Request}
import controllers.other.gambling.pbd.{routes => pbdRoutes}
import models.other.gambling.pbd.DoYouHavePBDRegistration
import utils.{Enrolments, NextPage}

trait DoYouHavePBDRegistrationNextPage {

  implicit val doYouHavePBDRegistration: NextPage[DoYouHavePBDRegistrationId.type, DoYouHavePBDRegistration, Call] = {
    new NextPage[DoYouHavePBDRegistrationId.type, DoYouHavePBDRegistration, Call] {
      override def get(b: DoYouHavePBDRegistration)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHavePBDRegistration.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.PoolBetting))
          case DoYouHavePBDRegistration.No  => pbdRoutes.RegisterGTSFirstController.onPageLoad()
        }
    }
  }
}
