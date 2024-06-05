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

package utils.nextpage.sa.partnership

import models.sa.partnership.HaveYouRegisteredPartnership
import uk.gov.hmrc.auth.core.Enrolments
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class HaveYouRegisteredPartnershipNextPageSpec extends NextPageSpecBase {

  "SA Partnership Other" when {
    behave like nextPageWithEnrolments(
      NextPage.haveYouRegisteredPartnership,
      (HaveYouRegisteredPartnership.Yes, Enrolments(Set())),
      HaveYouRegisteredPartnership.Yes.toString,
      "http://localhost:9555/enrolment-management-frontend/IR-SA-PART-ORG/request-access-tax-scheme?continue=%2Fbusiness-account",
      "no SA or CT Enrolments"
    )

    behave like nextPageWithEnrolments(
      NextPage.haveYouRegisteredPartnership,
      (HaveYouRegisteredPartnership.No, Enrolments(Set())),
      HaveYouRegisteredPartnership.No.toString,
      "https://assets.publishing.service.gov.uk/media/65d31d930f4eb1ec90a98110/SA400-2023.pdf",
      "no SA or CT Enrolments"
    )

    behave like nextPageWithEnrolments(
      NextPage.haveYouRegisteredPartnership,
      (HaveYouRegisteredPartnership.No, Enrolments(Set(saEnrolment))),
      HaveYouRegisteredPartnership.No.toString,
      "/digital-forms/form/register-a-partner-or-a-partnership-for-self-assessment/draft/guide",
      "SA Enrolment"
    )

    behave like nextPageWithEnrolments(
      NextPage.haveYouRegisteredPartnership,
      (HaveYouRegisteredPartnership.No, Enrolments(Set(ctEnrolment))),
      HaveYouRegisteredPartnership.No.toString,
      "/digital-forms/form/register-a-partner-or-a-partnership-for-self-assessment/draft/guide",
      "CT Enrolment"
    )

    behave like nextPageWithEnrolments(
      NextPage.haveYouRegisteredPartnership,
      (HaveYouRegisteredPartnership.No, Enrolments(Set(saEnrolment, ctEnrolment))),
      HaveYouRegisteredPartnership.No.toString,
      "/digital-forms/form/register-a-partner-or-a-partnership-for-self-assessment/draft/guide",
      "SA and CT Enrolment"
    )

  }

}
