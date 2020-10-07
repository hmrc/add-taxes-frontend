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

package utils.nextpage.vat.moss

import models.vat.moss.WhereIsYourBusinessBased
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class WhereIsYourBusinessBasedNextPageSpec extends NextPageSpecBase {

  "whereIsYourBusinessBased" when {
    behave like nextPage(
      NextPage.whereIsYourBusinessBased,
      WhereIsYourBusinessBased.UK,
      "/business-account/add-tax/vat/moss/uk"
    )

    behave like nextPage(
      NextPage.whereIsYourBusinessBased,
      WhereIsYourBusinessBased.EU,
      "/business-account/add-tax/vat/moss/eu"
    )

    behave like nextPage(
      NextPage.whereIsYourBusinessBased,
      WhereIsYourBusinessBased.NonEu,
      "/business-account/add-tax/vat/moss/non-eu"
    )

    behave like nextPage(
      NextPage.whereIsYourBusinessBased,
      WhereIsYourBusinessBased.Iom,
      "/business-account/add-tax/vat/moss/iom"
    )
  }
}
