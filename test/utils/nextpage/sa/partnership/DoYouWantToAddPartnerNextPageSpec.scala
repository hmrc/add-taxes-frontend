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

import utils.NextPage
import utils.nextpage.NextPageSpecBase
import models.sa.partnership.DoYouWantToAddPartner

class DoYouWantToAddPartnerNextPageSpec extends NextPageSpecBase {

  "SA Partnership" when {
    behave like nextPage(
      NextPage.doYouWantToAddPartner,
      DoYouWantToAddPartner.Yes,
      "https://assets.publishing.service.gov.uk/government/uploads/system/uploads/attachment_data/file/359508/sa401-static.pdf"
    )

    behave like nextPage(
      NextPage.doYouWantToAddPartner,
      DoYouWantToAddPartner.No,
      "/business-account/add-tax/self-assessment/partnership/other"
    )
  }
}
