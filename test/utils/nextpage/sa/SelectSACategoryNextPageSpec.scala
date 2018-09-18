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

import models.sa.{DoYouHaveSAUTR, SelectSACategory}
import uk.gov.hmrc.auth.core.AffinityGroup.{Individual, Organisation}
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class SelectSACategoryNextPageSpec extends NextPageSpecBase {

  "Self Assessment" when {

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Sa, DoYouHaveSAUTR.Yes, Some(Organisation)),
      SelectSACategory.Sa.toString,
      "http://localhost:9555/enrolment-management-frontend/IR-SA/request-access-tax-scheme?continue=%2Fbusiness-account",
      "organisation"
    )

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Sa, DoYouHaveSAUTR.No, Some(Organisation)),
      SelectSACategory.Sa.toString,
      "http://localhost:8080/portal/business-registration/select-taxes?lang=eng",
      "organisation"
    )

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Sa, DoYouHaveSAUTR.No, Some(Individual)),
      SelectSACategory.Sa.toString,
      "/business-account/add-tax/self-assessment/self-employed",
      "individual"
    )

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Partnership, DoYouHaveSAUTR.Yes, Some(Organisation)),
      SelectSACategory.Partnership.toString,
      "/business-account/add-tax/self-assessment/partnership",
      "organisation"
    )

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Partnership, DoYouHaveSAUTR.Yes, Some(Individual)),
      SelectSACategory.Partnership.toString,
      "/business-account/add-tax/self-assessment/partnership/new-account",
      "individual"
    )

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Trust, DoYouHaveSAUTR.Yes, Some(Organisation)),
      SelectSACategory.Trust.toString,
      "/business-account/add-tax/self-assessment/trust",
      "oranisation"
    )

    behave like nextPageWithAffinityGroup(
      NextPage.selectSACategory,
      (SelectSACategory.Trust, DoYouHaveSAUTR.Yes, Some(Individual)),
      SelectSACategory.Trust.toString,
      "/business-account/add-tax/self-assessment/trust/new-account",
      "individual"
    )
  }
}
