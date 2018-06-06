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

package utils.nextpage.employer.cis.uk

import models.employer.cis.uk.AreYouContractorOrSubcontractor
import uk.gov.hmrc.auth.core.Enrolments
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class AreYouContractorOrSubcontractorNextPageSpec extends NextPageSpecBase {

  "areYouContractorOrSubcontractor" when {

    behave like nextPageWithEnrolments(
      NextPage.areYouContractorOrSubcontractor,
      (AreYouContractorOrSubcontractor.Contractor, Enrolments(Set())),
      AreYouContractorOrSubcontractor.Contractor.toString,
      "/business-account/add-tax/employer/cis/uk/contractor",
      "no Enrolments"
    )

    behave like nextPageWithEnrolments(
      NextPage.areYouContractorOrSubcontractor,
      (AreYouContractorOrSubcontractor.Contractor, Enrolments(Set(epayeEnrolment))),
      AreYouContractorOrSubcontractor.Contractor.toString,
      "http://localhost:9555/enrolment-management-frontend/HMRC-CIS-ORG/request-access-tax-scheme?continue=%2Fbusiness-account",
      "EPAYE Enrolments"
    )

    behave like nextPageWithEnrolments(
      NextPage.areYouContractorOrSubcontractor,
      (AreYouContractorOrSubcontractor.Subcontractor, Enrolments(Set())),
      AreYouContractorOrSubcontractor.Subcontractor.toString,
      "/business-account/add-tax/employer/cis/uk/subcontractor",
      "no Enrolment"
    )
  }
}
