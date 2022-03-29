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

package utils.nextpage.vat

import models.vat.RegisterForVATOnline
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class RegisterForVATOnlineNextPageSpec extends NextPageSpecBase {

  "registerForVATOnline" when {
    behave like nextPage(
      NextPage.registerForVATOnline,
      RegisterForVATOnline.Yes,
      "http://localhost:8081/portal/business-registration/select-taxes?lang=eng"
    )

    behave like nextPage(
      NextPage.registerForVATOnline,
      RegisterForVATOnline.No,
      "/business-account/add-tax/vat/register-form"
    )
  }
}
