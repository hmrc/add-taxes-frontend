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

package controllers

import controllers.actions.{DataRequiredAction, DataRetrievalAction}
import play.api.test.Helpers._
import viewmodels.AnswerSection
import views.html.check_your_answers

class CheckYourAnswersControllerSpec extends ControllerSpecBase {

  val view: check_your_answers = injector.instanceOf[check_your_answers]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): CheckYourAnswersController = {
    new CheckYourAnswersController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      dataRetrievalAction,
      injector.instanceOf[DataRequiredAction],
      view
    )
  }

  "Check Your Answers Controller" must {
    "return 200 and the correct view for a GET" in {
      val expected = new check_your_answers(formWithCSRF, mainTemplate)(frontendAppConfig, Seq(AnswerSection(None, Seq())))(fakeRequest, messages).toString
      val result = controller().onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe expected
    }

    "redirect to Session Expired for a GET if not existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
