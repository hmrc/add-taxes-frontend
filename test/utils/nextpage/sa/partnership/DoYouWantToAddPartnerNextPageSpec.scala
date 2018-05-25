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
import models.sa.partnership.DoYouWantToAddPartner
import play.api.mvc.{AnyContent, AnyContentAsEmpty}
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import utils.HmrcEnrolmentType
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class DoYouWantToAddPartnerNextPageSpec extends NextPageSpecBase {

  val serviceRequest: ServiceInfoRequest[AnyContent] =
    ServiceInfoRequest(AuthenticatedRequest(request, "", Enrolments(Set()), None), Html(""))

  val saEnrolment = Enrolment(key = HmrcEnrolmentType.SA, identifiers = Seq(), state = "Activated")

  val ctEnrolment = Enrolment(key = HmrcEnrolmentType.CORP_TAX, identifiers = Seq(), state = "Activated")

  val serviceRequestWithSAEnrolment: ServiceInfoRequest[AnyContent] =
    ServiceInfoRequest(AuthenticatedRequest(request, "", Enrolments(Set(saEnrolment)), None), Html(""))

  val serviceRequestWithCTEnrolment: ServiceInfoRequest[AnyContent] =
    ServiceInfoRequest(AuthenticatedRequest(request, "", Enrolments(Set(ctEnrolment)), None), Html(""))

  val serviceRequestWithSACTEnrolment: ServiceInfoRequest[AnyContent] =
    ServiceInfoRequest(AuthenticatedRequest(request, "", Enrolments(Set(saEnrolment, ctEnrolment)), None), Html(""))

  "SA Partnership" when {
    behave like nextPageWithEnrolments(
      NextPage.doYouWantToAddPartner,
      (DoYouWantToAddPartner.Yes, serviceRequest),
      DoYouWantToAddPartner.Yes.toString,
      "https://assets.publishing.service.gov.uk/government/uploads/system/uploads/attachment_data/file/359508/sa401-static.pdf",
      "no SA or CT Enrolments"
    )

    behave like nextPageWithEnrolments(
      NextPage.doYouWantToAddPartner,
      (DoYouWantToAddPartner.No, serviceRequest),
      DoYouWantToAddPartner.No.toString,
      "/business-account/add-tax/self-assessment/partnership/other",
      "no SA or CT Enrolments"
    )

    behave like nextPageWithEnrolments(
      NextPage.doYouWantToAddPartner,
      (DoYouWantToAddPartner.Yes, serviceRequestWithSAEnrolment),
      DoYouWantToAddPartner.Yes.toString,
      "/forms/form/register-a-partner-or-a-partnership-for-self-assessment/start#1",
      "SA Enrolment"
    )

    behave like nextPageWithEnrolments(
      NextPage.doYouWantToAddPartner,
      (DoYouWantToAddPartner.Yes, serviceRequestWithCTEnrolment),
      DoYouWantToAddPartner.Yes.toString,
      "/forms/form/register-a-partner-or-a-partnership-for-self-assessment/start#1",
      "CT Enrolment"
    )

    behave like nextPageWithEnrolments(
      NextPage.doYouWantToAddPartner,
      (DoYouWantToAddPartner.Yes, serviceRequestWithSACTEnrolment),
      DoYouWantToAddPartner.Yes.toString,
      "/forms/form/register-a-partner-or-a-partnership-for-self-assessment/start#1",
      "SA and CT Enrolment"
    )

  }
}
