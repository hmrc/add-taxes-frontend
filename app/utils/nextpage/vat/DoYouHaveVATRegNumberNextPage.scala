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

package utils.nextpage.vat

import config.FrontendAppConfig
import controllers.vat.{routes => vatRoutes}
import identifiers.DoYouHaveVATRegNumberId
import models.vat.DoYouHaveVATRegNumber
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.NextPage

trait DoYouHaveVATRegNumberNextPage {

  type DoYouHaveVATRegNumberWithRequests = (DoYouHaveVATRegNumber, Option[AffinityGroup])

  implicit val doYouHaveVATRegNumber
    : NextPage[DoYouHaveVATRegNumberId.type, DoYouHaveVATRegNumberWithRequests, Call] = {
    new NextPage[DoYouHaveVATRegNumberId.type, DoYouHaveVATRegNumberWithRequests, Call] {
      override def get(b: DoYouHaveVATRegNumberWithRequests)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case (DoYouHaveVATRegNumber.Yes, _) => vatRoutes.WhatIsYourVATRegNumberController.onPageLoad()
          case (DoYouHaveVATRegNumber.No, _) => Call("GET", appConfig.vatRegHandoff)
        }
    }
  }
}
