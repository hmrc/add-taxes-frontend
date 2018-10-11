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

package utils.nextpage.vat

import models.vat.DoYouHaveVATRegNumber
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class DoYouHaveVATRegNumberNextPageSpec extends NextPageSpecBase {

  val affinityGroupOrganisation = Some(AffinityGroup.Organisation)
  val affinityGroupIndividual = Some(AffinityGroup.Individual)

  "doYouHaveVATRegNumber" when {
    behave like nextPage(
      NextPage.doYouHaveVATRegNumber,
      (DoYouHaveVATRegNumber.Yes, None),
      "/business-account/add-tax/vat/what-is-your-vat-number"
    )

    behave like nextPage(
      NextPage.doYouHaveVATRegNumber,
      (DoYouHaveVATRegNumber.No, affinityGroupOrganisation),
      "/business-account/add-tax/vat/register-online"
    )

    behave like nextPage(
      NextPage.doYouHaveVATRegNumber,
      (DoYouHaveVATRegNumber.No, affinityGroupIndividual),
      "/business-account/add-tax/vat/registered/no"
    )
  }
}
