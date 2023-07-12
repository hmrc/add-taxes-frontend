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

package utils.nextpage.other.gambling.mgd

import config.FrontendAppConfig
import identifiers.DoYouHaveMGDRegistrationId
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import utils.{Enrolments, NextPage}
import controllers.other.gambling.mgd.{routes => mgdRoutes}
import models.other.gambling.mgd.DoYouHaveMGDRegistration

trait DoYouHaveMGDRegistrationNextPage {

  implicit val doYouHaveMGDRegistration: NextPage[DoYouHaveMGDRegistrationId.type, DoYouHaveMGDRegistration, Call] = {
    new NextPage[DoYouHaveMGDRegistrationId.type, DoYouHaveMGDRegistration, Call] {
      override def get(b: DoYouHaveMGDRegistration)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case DoYouHaveMGDRegistration.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.MachineGamingDuty))
          case DoYouHaveMGDRegistration.No  => mgdRoutes.RegisterMGDController.onPageLoad()
        }
    }
  }
}
