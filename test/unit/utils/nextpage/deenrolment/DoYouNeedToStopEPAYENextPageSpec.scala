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

package utils.nextpage.deenrolment

import models.deenrolment.DoYouNeedToStopEPAYE
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class DoYouNeedToStopEPAYENextPageSpec extends NextPageSpecBase {

  "doYouNeedToStopEPAYE" when {
    behave like nextPage(
      NextPage.doYouNeedToStopEPAYE,
      DoYouNeedToStopEPAYE.Yes,
      "http://localhost:9020/business-account/epaye/remove"
    )

    behave like nextPage(
      NextPage.doYouNeedToStopEPAYE,
      DoYouNeedToStopEPAYE.No,
      "http://localhost:9555/enrolment-management-frontend/IR-PAYE/remove-access-tax-scheme?continue=%2Fbusiness-account"
    )
  }
}
