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

package utils.nextpage.deenrolment

import models.deenrolment.DoYouNeedToLeaveVATMOSS
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier}
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class DoYouNeedToLeaveVATMOSSNextPageSpec extends NextPageSpecBase {

  "doYouNeedToLeaveVATMOSS" when {
    val enrolment = Enrolment("HMRC-MOSS-U-ORG", List(EnrolmentIdentifier("ID", "1234567890")), "", None)

    behave like nextPage(
      NextPage.doYouNeedToLeaveVATMOSS,
      (DoYouNeedToLeaveVATMOSS.Yes, Some(enrolment)),
      Right("http://localhost:8081/portal/moss-variations/org/1234567890/change-reg-details?lang=eng")
    )

    behave like nextPage(
      NextPage.doYouNeedToLeaveVATMOSS,
      (DoYouNeedToLeaveVATMOSS.No, None: Option[Enrolment]),
      Right(
        "http://localhost:9555/enrolment-management-frontend/HMRC-MOSS-U-ORG/remove-access-tax-scheme?continue=%2Fbusiness-account")
    )

    behave like nextPage(
      NextPage.doYouNeedToLeaveVATMOSS,
      (DoYouNeedToLeaveVATMOSS.Yes, None: Option[Enrolment]),
      Left("unable to find enrolment")
    )

    val enrolmentWithNoIdentifier = enrolment.copy(identifiers = List.empty)

    behave like nextPage(
      NextPage.doYouNeedToLeaveVATMOSS,
      (DoYouNeedToLeaveVATMOSS.Yes, Some(enrolmentWithNoIdentifier)),
      Left(s"unable to find identifier for ${enrolmentWithNoIdentifier.key}")
    )
  }
}
