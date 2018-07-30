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

package utils.nextpage.employer.cis.uk.subcontractor

import config.FrontendAppConfig
import controllers.employer.cis.ukbased.subcontractor.routes
import identifiers.WhatTypeOfSubcontractorId
import models.employer.cis.uk.subcontractor.WhatTypeOfSubcontractor
import play.api.mvc.{Call, Request}
import uk.gov.hmrc.auth.core.Enrolments
import utils.{HmrcEnrolmentType, NextPage}

trait WhatTypeOfSubcontractorNextPage {

  type WhatTypeOfSubcontractorWithEnrolments = (WhatTypeOfSubcontractor, Enrolments)

  implicit val whatTypeOfSubcontractor
    : NextPage[WhatTypeOfSubcontractorId.type, WhatTypeOfSubcontractorWithEnrolments, Call] = {
    new NextPage[WhatTypeOfSubcontractorId.type, WhatTypeOfSubcontractorWithEnrolments, Call] {
      override def get(
        b: WhatTypeOfSubcontractorWithEnrolments)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call = {

        val (whatTypeOfSubcontractor, enrolments) = b

        (whatTypeOfSubcontractor, enrolments) match {
          case (WhatTypeOfSubcontractor.SoleTrader, HmrcEnrolmentType.SA() | HmrcEnrolmentType.CORP_TAX()) =>
            Call("GET", appConfig.getIFormUrl("cisSoleTrader"))

          case (WhatTypeOfSubcontractor.SoleTrader, _) =>
            routes.WasTurnoverMoreAfterVATController.onPageLoad()

          case (WhatTypeOfSubcontractor.Partnership, HmrcEnrolmentType.SA() | HmrcEnrolmentType.CORP_TAX()) =>
            Call("GET", appConfig.getIFormUrl("cisPartnership"))

          case (WhatTypeOfSubcontractor.Partnership, _) =>
            Call("GET", appConfig.getGovUKUrl("cisPartnershipReg"))

          case (WhatTypeOfSubcontractor.LimitedCompany, _) =>
            Call("GET", appConfig.getGovUKUrl("cisCompanyReg"))
        }
      }
    }
  }
}
