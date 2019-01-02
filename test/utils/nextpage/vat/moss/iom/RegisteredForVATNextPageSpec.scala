/*
 * Copyright 2019 HM Revenue & Customs
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

package utils.nextpage.vat.moss.iom

import models.vat.moss.iom.RegisteredForVAT
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class RegisteredForVATNextPageSpec extends NextPageSpecBase {

  "registeredForVAT" when {
    behave like nextPage(
      NextPage.registeredForVAT,
      RegisteredForVAT.No,
      "/business-account/add-tax/vat/moss/iom/not-vat-registered"
    )

    behave like nextPage(
      NextPage.registeredForVAT,
      RegisteredForVAT.Yes,
      "/business-account/add-tax/vat/moss/iom/vat-registered"
    )
  }
}
