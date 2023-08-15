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

package utils.nextpage.other.land

import models.other.land.SelectATax
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class SelectATaxNextPageSpec extends NextPageSpecBase {

  "selectATax" when {
    behave like nextPage(
      NextPage.selectATax,
      SelectATax.ATED,
      "/ated/home?callerId=bta"
    )

    behave like nextPage(
      NextPage.selectATax,
      SelectATax.LBT,
      "https://revenue.scot/taxes/land-buildings-transaction-tax/paper-lbtt-forms"
    )

    behave like nextPage(
      NextPage.selectATax,
      SelectATax.SDLT,
      "/business-account/add-tax/other/land/stamp-duty"
    )
  }
}
