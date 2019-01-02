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

package utils.nextpage.other.gambling

import models.other.gambling.SelectGamblingOrGamingDuty
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class SelectGamblingOrGamingDutyNextPageSpec extends NextPageSpecBase {

  "Gambling or Gaming" when {
    behave like nextPage(
      NextPage.selectGamblingOrGamingDuty,
      SelectGamblingOrGamingDuty.MGD,
      "/business-account/add-tax/other/gambling/mgd"
    )

    behave like nextPage(
      NextPage.selectGamblingOrGamingDuty,
      SelectGamblingOrGamingDuty.GBD,
      "/business-account/add-tax/other/gambling/gbd"
    )

    behave like nextPage(
      NextPage.selectGamblingOrGamingDuty,
      SelectGamblingOrGamingDuty.PBD,
      "/business-account/add-tax/other/gambling/pbd"
    )

    behave like nextPage(
      NextPage.selectGamblingOrGamingDuty,
      SelectGamblingOrGamingDuty.RGD,
      "/business-account/add-tax/other/gambling/rgd"
    )
  }
}
