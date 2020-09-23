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

package utils.nextpage.wrongcredentials

import models.wrongcredentials.FindingYourAccount
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class FindingYourAccountNextPageSpec extends NextPageSpecBase {

  "FindingYourAccountFormProvider" when {
    def lostCredentialsUrlGenerator(forgottenOption: String): String =
      s"http://localhost:9553/bas-gateway/cred-recovery?continue_url=/account&recovery=$forgottenOption"

    behave like nextPage(
      NextPage.findingYourAccount,
      FindingYourAccount.DontKnowId,
      lostCredentialsUrlGenerator("userid"))

    behave like nextPage(
      NextPage.findingYourAccount,
      FindingYourAccount.DontKnowPassword,
      lostCredentialsUrlGenerator("password"))

    behave like nextPage(
      NextPage.findingYourAccount,
      FindingYourAccount.DontKnowIdOrPassword,
      lostCredentialsUrlGenerator("both"))
  }
}
