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

package utils.nextpage.other.aeoi

import config.FrontendAppConfig
import controllers.other.aeoi.{routes => aeoiRoutes}
import models.other.aeoi.HaveYouRegisteredAEOI
import utils.{Enrolments, NextPage}
import identifiers.HaveYouRegisteredAEOIId
import models.requests.ServiceInfoRequest
import play.api.mvc.Call

trait HaveYouRegisteredAEOINextPage {

  implicit val haveYouRegisteredAEOI: NextPage[HaveYouRegisteredAEOIId.type, HaveYouRegisteredAEOI, Call] = {
    new NextPage[HaveYouRegisteredAEOIId.type, HaveYouRegisteredAEOI, Call] {
      override def get(b: HaveYouRegisteredAEOI)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case HaveYouRegisteredAEOI.Yes =>
            Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.AEOI))
          case HaveYouRegisteredAEOI.No => aeoiRoutes.RegisterAEOIController.onPageLoad()
        }
    }
  }

}
