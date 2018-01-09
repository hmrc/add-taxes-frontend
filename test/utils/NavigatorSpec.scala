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
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import controllers.routes
import identifiers._
import models.HaveYouRegisteredForTiedOils.No
import models.SelectAnOilService.TiedOilsEnquiryService
import models._

class NavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {
      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }

      "go to 'Have You Registered Tied Oils?' page from 'Select Oil Service' when the user answers Tied Oil" in {
        val answers = mock[UserAnswers]
        when(answers.selectAnOilService) thenReturn Some(TiedOilsEnquiryService)
        navigator.nextPage(SelectAnOilServiceId, NormalMode)(answers) mustBe routes.HaveYouRegisteredForTiedOilsController.onPageLoad(NormalMode)
      }

      "go to the 'Register for Tied Oils Enquiry Service' page from 'Have You Registered Tied Oils?' when the user answers No" in {
        val answers = mock[UserAnswers]
        when(answers.haveYouRegisteredForTiedOils) thenReturn Some(No)
        navigator.nextPage(HaveYouRegisteredForTiedOilsId, NormalMode)(answers) mustBe routes.RegisterTiedOilsController.onPageLoad()
      }
    }

    "in Check mode" must {
      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode)(mock[UserAnswers]) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
