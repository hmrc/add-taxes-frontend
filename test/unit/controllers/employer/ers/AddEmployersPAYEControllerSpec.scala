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
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.employer.ers.addEmployersPAYE

class AddEmployersPAYEControllerSpec extends ControllerSpecBase {

  val view: addEmployersPAYE = injector.instanceOf[addEmployersPAYE]

  def controller(): AddEmployersPAYEController = {
    new AddEmployersPAYEController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      view
    )
  }

  def viewAsString(): String =
    new addEmployersPAYE(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "AddEmployersPAYE Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
