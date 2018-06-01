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
import controllers.employer.cis.uk.subcontractor.routes
import identifiers.WhatTypeOfSubcontractorId
import models.employer.cis.uk.subcontractor.WhatTypeOfSubcontractor
import play.api.mvc.{Call, Request}
import utils.NextPage

trait WhatTypeOfSubcontractorNextPage {

  implicit val whatTypeOfSubcontractor: NextPage[WhatTypeOfSubcontractorId.type, WhatTypeOfSubcontractor] = {
    new NextPage[WhatTypeOfSubcontractorId.type, WhatTypeOfSubcontractor] {
      override def get(b: WhatTypeOfSubcontractor)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case WhatTypeOfSubcontractor.SoleTrader     => routes.WasTurnoverMoreAfterVATController.onPageLoad()
          case WhatTypeOfSubcontractor.Partnership    => Call("GET", appConfig.getGovUKUrl("cisPartnershipReg"))
          case WhatTypeOfSubcontractor.LimitedCompany => Call("GET", appConfig.getGovUKUrl("cisCompanyReg"))
        }
    }
  }
}
