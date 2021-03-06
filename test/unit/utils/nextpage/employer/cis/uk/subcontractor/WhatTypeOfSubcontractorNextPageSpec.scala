/*
 * Copyright 2020 HM Revenue & Customs
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

import models.employer.cis.uk.subcontractor.WhatTypeOfSubcontractor
import uk.gov.hmrc.auth.core.Enrolments
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class WhatTypeOfSubcontractorNextPageSpec extends NextPageSpecBase {

  "whatTypeOfSubcontractor" when {

    val nextPageSoleTraderSAValue = (WhatTypeOfSubcontractor.SoleTrader, Enrolments(Set(saEnrolment)))

    behave like nextPageWithEnrolments(
      NextPage.whatTypeOfSubcontractor,
      nextPageSoleTraderSAValue,
      WhatTypeOfSubcontractor.SoleTrader.toString,
      "/digital-forms/form/CIS301302/draft/guide",
      "SA Enrolment"
    )

    val nextPageSoleTraderCTValue = (WhatTypeOfSubcontractor.SoleTrader, Enrolments(Set(ctEnrolment)))

    behave like nextPageWithEnrolments(
      NextPage.whatTypeOfSubcontractor,
      nextPageSoleTraderCTValue,
      WhatTypeOfSubcontractor.SoleTrader.toString,
      "/digital-forms/form/CIS301302/draft/guide",
      "CT Enrolment"
    )

    val nextPageSoleTraderNoEnrolmentValue = (WhatTypeOfSubcontractor.SoleTrader, Enrolments(Set()))

    behave like nextPageWithEnrolments(
      NextPage.whatTypeOfSubcontractor,
      nextPageSoleTraderNoEnrolmentValue,
      WhatTypeOfSubcontractor.SoleTrader.toString,
      "/business-account/add-tax/employer/cis/uk/subcontractor/sole-trader",
      "no Enrolment"
    )

    val nextPageSoleTraderTwoEnrolmentValue =
      (WhatTypeOfSubcontractor.SoleTrader, Enrolments(Set(saEnrolment, ctEnrolment)))

    behave like nextPageWithEnrolments(
      NextPage.whatTypeOfSubcontractor,
      nextPageSoleTraderTwoEnrolmentValue,
      WhatTypeOfSubcontractor.SoleTrader.toString,
      "/digital-forms/form/CIS301302/draft/guide",
      "two Enrolments"
    )

    val nextPagePartnershipSAValue = (WhatTypeOfSubcontractor.Partnership, Enrolments(Set(saEnrolment)))

    behave like nextPageWithEnrolments(
      NextPage.whatTypeOfSubcontractor,
      nextPagePartnershipSAValue,
      WhatTypeOfSubcontractor.Partnership.toString,
      "/digital-forms/form/Construction-Industry-Scheme-register-your-partnership/draft/guide",
      "SA Enrolment"
    )

    val nextPagePartnershipCTValue = (WhatTypeOfSubcontractor.Partnership, Enrolments(Set(ctEnrolment)))

    behave like nextPageWithEnrolments(
      NextPage.whatTypeOfSubcontractor,
      nextPagePartnershipCTValue,
      WhatTypeOfSubcontractor.Partnership.toString,
      "/digital-forms/form/Construction-Industry-Scheme-register-your-partnership/draft/guide",
      "CT Enrolment"
    )

    val nextPagePartnershipNoValue = (WhatTypeOfSubcontractor.Partnership, Enrolments(Set()))

    behave like nextPageWithEnrolments(
      NextPage.whatTypeOfSubcontractor,
      nextPagePartnershipNoValue,
      WhatTypeOfSubcontractor.Partnership.toString,
      "https://www.gov.uk/government/publications/construction-industry-scheme-partnership-registration-cis304",
      "no Enrolment"
    )

    val nextPageLimitedCompanySAValue = (WhatTypeOfSubcontractor.LimitedCompany, Enrolments(Set(saEnrolment)))

    behave like nextPageWithEnrolments(
      NextPage.whatTypeOfSubcontractor,
      nextPageLimitedCompanySAValue,
      WhatTypeOfSubcontractor.LimitedCompany.toString,
      "https://www.gov.uk/government/publications/construction-industry-scheme-company-registration-cis305",
      "SA Enrolment"
    )

    val nextPageLimitedCompanyCTValue = (WhatTypeOfSubcontractor.LimitedCompany, Enrolments(Set(ctEnrolment)))

    behave like nextPageWithEnrolments(
      NextPage.whatTypeOfSubcontractor,
      nextPageLimitedCompanyCTValue,
      WhatTypeOfSubcontractor.LimitedCompany.toString,
      "https://www.gov.uk/government/publications/construction-industry-scheme-company-registration-cis305",
      "CT Enrolment"
    )

    val nextPageLimitedCompanyNoValue = (WhatTypeOfSubcontractor.LimitedCompany, Enrolments(Set()))

    behave like nextPageWithEnrolments(
      NextPage.whatTypeOfSubcontractor,
      nextPageLimitedCompanyNoValue,
      WhatTypeOfSubcontractor.LimitedCompany.toString,
      "https://www.gov.uk/government/publications/construction-industry-scheme-company-registration-cis305",
      "no Enrolment"
    )
  }
}
