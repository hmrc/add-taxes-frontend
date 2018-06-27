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

package utils.nextpage.other.gambling.rgd

import config.FrontendAppConfig
import controllers.other.gambling.rgd.routes
import identifiers.DoYouHaveRGDRegistrationId
import play.api.mvc.{Call, Request}
import models.other.gambling.rgd.DoYouHaveRGDRegistration
import utils.{Enrolments, NextPage}

trait DoYouHaveRGDRegistrationNextPage {

  implicit val doYouHaveRGDRegistration: NextPage[DoYouHaveRGDRegistrationId.type, DoYouHaveRGDRegistration] = {
    new NextPage[DoYouHaveRGDRegistrationId.type, DoYouHaveRGDRegistration] {
      override def get(b: DoYouHaveRGDRegistration)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveRGDRegistration.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.RemoteGaming))
          case DoYouHaveRGDRegistration.No  => routes.RegisterRGDController.onPageLoad()
        }
    }
  }
}
