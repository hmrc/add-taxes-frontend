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

package utils

import base.SpecBase
import identifiers.{HaveYouRegisteredForTiedOilsId, SelectAnOilServiceId}
import models.HaveYouRegisteredForTiedOils.Yes
import models.{HaveYouRegisteredForTiedOils, SelectAnOilService}
import models.SelectAnOilService.TiedOilsEnquiryService


class NextPageSpec extends SpecBase {

  val navigator = new Navigator

  "Next Page" when {
    "given SelectAnOilService and TiedOilsEnquiryService type" should {
      "direct to correct url" in {
        val result = navigator.nextPage[SelectAnOilServiceId.type, SelectAnOilService](SelectAnOilServiceId, TiedOilsEnquiryService)(NextPage.selectAnOilService)
        result.url mustBe "/add-taxes-frontend/haveYouRegisteredForTiedOils"
      }
    }
    "given TiedOilsEnquiryService type" should {
      "direct to correct url" in {
        val result = navigator.nextPage[HaveYouRegisteredForTiedOilsId.type, HaveYouRegisteredForTiedOils](HaveYouRegisteredForTiedOilsId, Yes)(NextPage.haveYouRegisteredForTiedOils)
        result.url mustBe "/add-taxes-frontend"
      }
    }
  }
}
