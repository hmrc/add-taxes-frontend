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

package utils.nextpage.vat.moss.uk

import models.vat.moss.uk.OnlineVATAccount
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class OnlineVATAccountNextPageSpec extends NextPageSpecBase {

  "VAT MOSS UK online VAT Account" when {
    behave like nextPage(
      NextPage.onlineVATAccount,
      OnlineVATAccount.Yes,
      "/business-account/add-tax/vat/moss/uk/vat-registered/other-account"
    )

    behave like nextPage(
      NextPage.onlineVATAccount,
      OnlineVATAccount.No,
      "/business-account/add-tax/vat/moss/uk/vat-registered/no-other-account"
    )
  }
}
