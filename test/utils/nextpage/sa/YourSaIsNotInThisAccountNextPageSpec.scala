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

import models.sa.YourSaIsNotInThisAccount
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class YourSaIsNotInThisAccountNextPageSpec extends NextPageSpecBase {
  "The logic behind the YourSaIsNotInThisAccountNextPage form " should {
    behave like nextPage(
      NextPage.yourSaIsNotInThisAccount,
      YourSaIsNotInThisAccount.LookInOtherAccount,
      "http://localhost:9020/business-account/wrong-credentials"
    )

    behave like nextPage(
      NextPage.yourSaIsNotInThisAccount,
      YourSaIsNotInThisAccount.AddToThisAccount,
      "/business-account/add-tax/self-assessment"
    )
  }

}
