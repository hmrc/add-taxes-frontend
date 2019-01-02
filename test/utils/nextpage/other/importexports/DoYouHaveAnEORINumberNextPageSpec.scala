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

package utils.nextpage.other.importexports

import models.other.importexports.DoYouHaveEORINumber
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class DoYouHaveAnEORINumberNextPageSpec extends NextPageSpecBase {

  "icsEori" when {
    behave like nextPage(
      NextPage.icsEori,
      DoYouHaveEORINumber.Yes,
      "http://localhost:9555/enrolment-management-frontend/HMRC-ICS-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.icsEori,
      DoYouHaveEORINumber.No,
      "/business-account/add-tax/other/import-export/ics/register"
    )
  }

  "nesEori" when {
    behave like nextPage(
      NextPage.nesEori,
      DoYouHaveEORINumber.Yes,
      "/business-account/add-tax/other/import-export/nes/has-eori"
    )

    behave like nextPage(
      NextPage.nesEori,
      DoYouHaveEORINumber.No,
      "/business-account/add-tax/other/import-export/nes/no-eori"
    )
  }

}
