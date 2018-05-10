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
import play.api.mvc.Cookie

class UrlHelperSpec extends SpecBase {

  "registerForTaxUrl" should {
    "return enrolment-management-frontend/$enrolmentKey/request-access-tax-scheme?continue=%2Fbusiness-account" in {
      val SUT = new UrlHelper(frontendAppConfig)
      Enrolments.values.map { enrolment =>
        SUT.emacEnrollmentsUrl(enrolment) mustBe
          (s"http://localhost:9555/enrolment-management-frontend/${enrolment.toString}/" +
            "request-access-tax-scheme?continue=%2Fbusiness-account")
      }
    }
  }
  "governmentGatewayLostCredentialsUrl" should {
    "return correct url" in {
      val SUT = new UrlHelper(frontendAppConfig)
      ForgottenOptions.values.map { forgottenOption =>
        SUT.governmentGatewayLostCredentialsUrl(forgottenOption) mustBe
          s"http://localhost:9898/government-gateway-lost-credentials-frontend/" +
            s"choose-your-account?continue=%2Fbusiness-account&origin=business-tax-account&forgottenOption=${forgottenOption.toString}"
      }
    }
  }
  "businessTaxAccountLink" should {
    "return correct url" in {
      val SUT = new UrlHelper(frontendAppConfig)
      val destination = "alcohol"
        SUT.businessTaxAccountLink(destination) mustBe s"http://localhost:9020/business-account/add-tax/other/$destination"
      }
    }

  "fulfilmentHouse" should {
    "return correct url" in {
      val SUT = new UrlHelper(frontendAppConfig)
      SUT.fulfilmentHouse() mustBe "http://localhost:1118/fhdds"
    }
  }

  "build portal url" when {
    val fakeRequestWithWelsh = fakeRequest.withCookies(Cookie("PLAY_LANG", "cy"))

    "the user is in english" should {
      "append ?lang=eng to given url" in {
        new UrlHelper(frontendAppConfig).getPortalURL("novaEnrolment")(fakeRequest) mustBe "http://localhost:8080/portal/nova/normal?lang=eng"
      }
    }

    "the user is in welsh" should {
      "append ?lang=cym to given url" in {
        new UrlHelper(frontendAppConfig).getPortalURL("novaEnrolment")(fakeRequestWithWelsh) mustBe "http://localhost:8080/portal/nova/normal?lang=cym"
      }
    }
  }
}
