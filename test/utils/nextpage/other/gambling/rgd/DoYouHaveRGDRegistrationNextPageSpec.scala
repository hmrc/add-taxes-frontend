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

package utils.nextpage.other.gambling.rgd

import models.other.gambling.rgd.DoYouHaveRGDRegistration
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class DoYouHaveRGDRegistrationNextPageSpec extends NextPageSpecBase {

  "doYouHaveRGDRegistration" when {
    behave like nextPage(
      NextPage.doYouHaveRGDRegistration,
      DoYouHaveRGDRegistration.Yes,
      "http://localhost:9555/enrolment-management-frontend/HMRC-GTS-RGD/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.doYouHaveRGDRegistration,
      DoYouHaveRGDRegistration.No,
      "/business-account/add-tax/other/gambling/rgd/register"
    )
  }
}
