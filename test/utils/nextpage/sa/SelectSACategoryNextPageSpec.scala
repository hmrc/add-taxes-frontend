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

package utils.nextpage.sa

import models.sa.SelectSACategory
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.auth.core.AffinityGroup.{Individual, Organisation}
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class SelectSACategoryNextPageSpec extends NextPageSpecBase {

  "Self Assessment" when {

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Sa, Organisation),
      SelectSACategory.Sa.toString,
      "http://localhost:8080/portal/business-registration/introduction?lang=eng"
    )

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Partnership, Organisation),
      SelectSACategory.Partnership.toString,
      "/business-account/add-tax/self-assessment/partnership"
    )

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Trust, Organisation),
      SelectSACategory.Trust.toString,
      "/business-account/add-tax/self-assessment/trust"
    )

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Trust, Individual),
      SelectSACategory.Trust.toString,
      "/business-account/add-tax/self-assessment/trust/new-account",
      "Individual"
    )
  }
}
