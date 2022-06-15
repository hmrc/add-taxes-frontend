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
import forms.employer.IsBusinessRegisteredForPAYEFormProvider
import models.employer.IsBusinessRegisteredForPAYE
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.FakeNavigator
import viewmodels.ViewAction
import views.html.employer.isBusinessRegisteredForPAYE

class IsBusinessRegisteredForPAYEControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val formProvider = new IsBusinessRegisteredForPAYEFormProvider()
  val form: Form[IsBusinessRegisteredForPAYE] = formProvider()

  val view: isBusinessRegisteredForPAYE = injector.instanceOf[isBusinessRegisteredForPAYE]
  val viewAction: ViewAction = ViewAction(routes.IsBusinessRegisteredForPAYEController.onSubmit(), "AddErsEpayeRegistered")

  def controller(): IsBusinessRegisteredForPAYEController = {
    new IsBusinessRegisteredForPAYEController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeServiceInfoAction,
      formProvider,
      view
    )
  }

  def viewAsString(form: Form[_] = form): String =
    new isBusinessRegisteredForPAYE(formWithCSRF, mainTemplate)(frontendAppConfig, form, viewAction)(HtmlFormat.empty)(fakeRequest, messages).toString()

  "IsBusinessRegisteredForPAYE Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> "Yes").withMethod("POST")
      val result = controller().onSubmit(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return BadRequest when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> "").withMethod("POST")
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller().onSubmit(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }
  }
}
