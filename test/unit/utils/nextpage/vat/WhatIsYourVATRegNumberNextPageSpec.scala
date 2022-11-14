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

package utils.nextpage.vat

import play.api.http.Status.{NOT_FOUND, OK}
import utils.NextPage
import utils.nextpage.NextPageSpecBase
class WhatIsYourVATRegNumberNextPageSpec extends NextPageSpecBase {

  "whatIsYourVATRegNumber" when {
    behave like nextPage(
      NextPage.whatIsYourVATRegNumber,
      (OK, "999999999"),
      "http://localhost:9936/claim-vat-enrolment/journey/999999999?continueUrl=/vat-through-software/sign-up/sign-up-complete-client"
    )

    behave like nextPage(
      NextPage.whatIsYourVATRegNumber,
      (NOT_FOUND, "999999999"),
      "http://localhost:9555/enrolment-management-frontend/HMCE-VATDEC-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )
  }
}
