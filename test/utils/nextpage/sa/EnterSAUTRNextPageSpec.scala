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

import models.sa.SAUTR
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class EnterSAUTRNextPageSpec extends NextPageSpecBase {

  "EnterSAUTRNextPage" when {

    behave like nextPage(
      NextPage.enterSAUTR,
      SAUTR("0123456789"),
      "www.somehwere.com"
    )

  }

}

//     behave like nextPageWithAffinityGroup(
//NextPage.selectSACategory,
//(SelectSACategory.Sa, Some(Organisation)),
//SelectSACategory.Sa.toString,
//"http://localhost:8080/portal/business-registration/introduction?lang=eng",
//"organisation"
//)
