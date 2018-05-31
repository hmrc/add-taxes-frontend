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

package utils.nextpage.sa.partnership

import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.partnership.HaveYouRegisteredPartnership
import play.api.mvc.AnyContent
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}
import utils.HmrcEnrolmentType
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class HaveYouRegisteredPartnershipNextPageSpec extends NextPageSpecBase {

  "SA Partnership Other" when {
    behave like nextPageWithEnrolments(
      NextPage.haveYouRegisteredPartnership,
      (HaveYouRegisteredPartnership.Yes, serviceRequest),
      HaveYouRegisteredPartnership.Yes.toString,
      "http://localhost:9555/enrolment-management-frontend/IR-SA-PART-ORG/request-access-tax-scheme?continue=%2Fbusiness-account",
      "no SA or CT Enrolments"
    )

    behave like nextPageWithEnrolments(
      NextPage.haveYouRegisteredPartnership,
      (HaveYouRegisteredPartnership.No, serviceRequest),
      HaveYouRegisteredPartnership.No.toString,
      "https://assets.publishing.service.gov.uk/government/uploads/system/uploads/attachment_data/file/359500/sa400-static.pdf",
      "no SA or CT Enrolments"
    )

    behave like nextPageWithEnrolments(
      NextPage.haveYouRegisteredPartnership,
      (HaveYouRegisteredPartnership.No, createServiceRequest(Set(saEnrolment))),
      HaveYouRegisteredPartnership.No.toString,
      "/forms/form/register-a-partner-or-a-partnership-for-self-assessment/start#1",
      "SA Enrolment"
    )

    behave like nextPageWithEnrolments(
      NextPage.haveYouRegisteredPartnership,
      (HaveYouRegisteredPartnership.No, createServiceRequest(Set(ctEnrolment))),
      HaveYouRegisteredPartnership.No.toString,
      "/forms/form/register-a-partner-or-a-partnership-for-self-assessment/start#1",
      "CT Enrolment"
    )

    behave like nextPageWithEnrolments(
      NextPage.haveYouRegisteredPartnership,
      (HaveYouRegisteredPartnership.No, createServiceRequest(Set(saEnrolment, ctEnrolment))),
      HaveYouRegisteredPartnership.No.toString,
      "/forms/form/register-a-partner-or-a-partnership-for-self-assessment/start#1",
      "SA and CT Enrolment"
    )

  }

}
