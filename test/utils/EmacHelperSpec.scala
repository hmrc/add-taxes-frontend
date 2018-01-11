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

package utils

import base.SpecBase

class EmacHelperSpec extends SpecBase {
  "registerForTaxUrl" should {
    "return enrolment-management-frontend/$enrolmentKey/request-access-tax-scheme?continue=%2Fbusiness-account" in {
      val SUT = new EmacHelper(frontendAppConfig)
      Enrolments.values.map { enrolment =>
        SUT.registerForTaxUrl(enrolment) mustBe
          (s"http://localhost:9555/enrolment-management-frontend/${enrolment.toString}/" +
            "request-access-tax-scheme?continue=%2Fbusiness-account")
      }
    }
  }
}
