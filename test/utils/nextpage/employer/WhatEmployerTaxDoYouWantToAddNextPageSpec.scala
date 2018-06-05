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

package utils.nextpage.employer

import models.employer.WhatEmployerTaxDoYouWantToAdd
import uk.gov.hmrc.auth.core.Enrolments
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class WhatEmployerTaxDoYouWantToAddNextPageSpec extends NextPageSpecBase {

  "whatEmployerTaxDoYouWantToAdd" when {
    behave like nextPage(
      NextPage.whatEmployerTaxDoYouWantToAdd,
      WhatEmployerTaxDoYouWantToAdd.EPAYE,
      "http://localhost:8080/portal/business-registration/introduction?lang=eng"
    )

    behave like nextPage(
      NextPage.whatEmployerTaxDoYouWantToAdd,
      WhatEmployerTaxDoYouWantToAdd.CIS,
      "http://www.tax.service.gov.uk/business-account/add-tax/employer/cis"
    )

    behave like nextPage(
      NextPage.whatEmployerTaxDoYouWantToAdd,
      WhatEmployerTaxDoYouWantToAdd.PS,
      "/business-account/add-tax/employer/pension"
    )

    behave like nextPage(
      NextPage.whatEmployerTaxDoYouWantToAdd,
      WhatEmployerTaxDoYouWantToAdd.ERS,
      "/business-account/add-tax/employer/ers"
    )

    behave like nextPage(
      NextPage.whatEmployerTaxDoYouWantToAdd,
      WhatEmployerTaxDoYouWantToAdd.EIA,
      "/business-account/add-tax/employer/intermediaries"
    )
  }
}
