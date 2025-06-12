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

package utils.nextpage.employer.paye

import models.employer.paye.DoesBusinessHaveDirectorsOrPartners
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class DoesBusinessHaveDirectorsOrPartnersNextPageSpec extends NextPageSpecBase {

  "doesBusinessHaveDirectorsOrPartners" when {
    behave like nextPage(
      NextPage.doesBusinessHaveDirectorsOrPartners,
      DoesBusinessHaveDirectorsOrPartners.Director,
      "/business-account/add-tax/employer/directors"
    )

    behave like nextPage(
      NextPage.doesBusinessHaveDirectorsOrPartners,
      DoesBusinessHaveDirectorsOrPartners.Partner,
      "/business-account/add-tax/employer/partners"
    )

    behave like nextPagePortal(
      NextPage.doesBusinessHaveDirectorsOrPartners,
      DoesBusinessHaveDirectorsOrPartners.Solo,
      "/portal/business-registration/select-taxes"
    )
  }
}
