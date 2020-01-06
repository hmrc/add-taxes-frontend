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

package controllers.employer.ers

import controllers._
import controllers.actions._
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.employer.ers.useEmployersPAYE

class UseEmployersPAYEControllerSpec extends ControllerSpecBase {

  def controller() =
    new UseEmployersPAYEController(frontendAppConfig, messagesApi, FakeAuthAction, FakeServiceInfoAction)

  def viewAsString() = useEmployersPAYE(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "UseEmployersPAYE Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
