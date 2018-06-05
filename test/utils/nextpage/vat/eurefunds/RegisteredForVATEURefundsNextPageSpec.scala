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

package utils.nextpage.vat.eurefunds

import models.vat.RegisteredForVAT
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class RegisteredForVATEURefundsNextPageSpec extends NextPageSpecBase {

  "registeredForVATEURefunds" when {
    behave like nextPage(
      NextPage.registeredForVATEURefunds,
      RegisteredForVAT.Yes,
      "http://localhost:9555/enrolment-management-frontend/HMRC-EU-REF-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.registeredForVATECSales,
      RegisteredForVAT.No,
      "http://localhost:8080/portal/business-registration/introduction?lang=eng"
    )
  }
}
