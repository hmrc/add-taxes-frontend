/*
 * Copyright 2018 HM Revenue & Customs
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

package utils.nextpage.sa.partnership

import models.sa.partnership.HaveYouRegisteredPartnership
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class HaveYouRegisteredPartnershipNextPageSpec extends NextPageSpecBase {

  "SA Partnership Other" when {
    behave like nextPage(
      NextPage.haveYouRegisteredPartnership,
      HaveYouRegisteredPartnership.Yes,
      "http://localhost:9555/enrolment-management-frontend/IR-SA-PART-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.haveYouRegisteredPartnership,
      HaveYouRegisteredPartnership.No,
      "https://assets.publishing.service.gov.uk/government/uploads/system/uploads/attachment_data/file/359500/sa400-static.pdf"
    )
  }
}
