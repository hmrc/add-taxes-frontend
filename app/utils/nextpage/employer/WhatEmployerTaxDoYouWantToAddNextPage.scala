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

package utils.nextpage.employer

import config.FrontendAppConfig
import controllers.employer.ers.{routes => ersRoutes}
import controllers.employer.intermediaries.{routes => intRoutes}
import controllers.employer.cis.{routes => cisRoutes}
import identifiers.WhatEmployerTaxDoYouWantToAddId
import models.employer.WhatEmployerTaxDoYouWantToAdd
import play.api.mvc.{Call, Request}
import uk.gov.hmrc.auth.core.Enrolments
import utils.{HmrcEnrolmentType, NextPage}

trait WhatEmployerTaxDoYouWantToAddNextPage {

  type WhatEmployerTaxDoYouWantToAddWithEnrolment = (WhatEmployerTaxDoYouWantToAdd, Enrolments)

  implicit val whatEmployerTaxDoYouWantToAdd
    : NextPage[WhatEmployerTaxDoYouWantToAddId.type, WhatEmployerTaxDoYouWantToAddWithEnrolment, Call] = {
    new NextPage[WhatEmployerTaxDoYouWantToAddId.type, WhatEmployerTaxDoYouWantToAddWithEnrolment, Call] {
      override def get(details: WhatEmployerTaxDoYouWantToAddWithEnrolment)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        details match {
          case (WhatEmployerTaxDoYouWantToAdd.EPAYE, _) =>
            Call("GET", appConfig.getPortalUrl("businessRegistration"))
          case (WhatEmployerTaxDoYouWantToAdd.CIS, _) =>
            cisRoutes.IsYourBusinessInUKController.onPageLoad()
          case (WhatEmployerTaxDoYouWantToAdd.PS, _) =>
            Call("GET", appConfig.getPortalUrl("pensionPractitioners"))
          case (WhatEmployerTaxDoYouWantToAdd.ERS, HmrcEnrolmentType.EPAYE()) =>
            Call("GET", appConfig.getPortalUrl("enrolERS"))
          case (WhatEmployerTaxDoYouWantToAdd.ERS, _) =>
            ersRoutes.IsBusinessRegisteredForPAYEController.onPageLoad()
          case (WhatEmployerTaxDoYouWantToAdd.EIA, HmrcEnrolmentType.EPAYE()) =>
            Call("GET", appConfig.eiUrl)
          case (WhatEmployerTaxDoYouWantToAdd.EIA, _) =>
            intRoutes.IsBusinessRegisteredForPAYEController.onPageLoad()
        }
    }
  }
}
