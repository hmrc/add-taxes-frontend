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

package utils.nextpage.sa

import play.api.test.FakeRequest
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class EnterSAUTRNextPageSpec extends NextPageSpecBase {

  "EnterSAUTRNextPage" when {

    behave like nextPage(
      NextPage.enterSAUTR,
      (false, true),
      "http://localhost:9020/business-account/wrong-credentials"
    )

    behave like nextPage(
      NextPage.enterSAUTR,
      (true, true),
      "http://localhost:9020/business-account/wrong-credentials"
    )

    behave like nextPage(
      NextPage.enterSAUTR,
      (false, false),
      "/business-account/add-tax/self-assessment"
    )

    behave like nextPage(
      NextPage.enterSAUTR,
      (true, false),
      "/business-account/add-tax/self-assessment/known-facts"
    )

    "called with a session variable tryingToAccessSa = true and the utr is not associated with another account" should {
      "redirect to the 'Your SA is not in this account' page" in {
        val result =
          NextPage.enterSAUTR.get(false, false)(
            frontendAppConfig,
            featureConfig,
            FakeRequest().withSession(("tryingToAccessSa", "true")))
        result.url mustBe "/business-account/add-tax/self-assessment/not-in-this-account"
      }
    }
  }

}
