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

package controllers.sa

import controllers.ControllerSpecBase
import controllers.actions.{FakeAuthAction, FakeServiceInfoAction}
import forms.sa.YourSaIsNotInThisAccountFormProvider
import models.sa.YourSaIsNotInThisAccount
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import utils.{FakeNavigator, HmrcEnrolmentType, RadioOption}
import views.html.sa.yourSaIsNotInThisAccount
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat

class YourSaIsNotInThisAccountControllerSpec extends ControllerSpecBase {

  def onwardRoute = controllers.sa.routes.SelectSACategoryController.onPageLoadHasUTR()

  val formProvider = new YourSaIsNotInThisAccountFormProvider()
  val form = formProvider()

  def controller()(enrolmentTypes: HmrcEnrolmentType*) = new YourSaIsNotInThisAccountController(
    frontendAppConfig,
    messagesApi,
    new FakeNavigator[Call](desiredRoute = onwardRoute),
    FakeAuthAction,
    FakeServiceInfoAction(enrolmentTypes: _*),
    formProvider
  )

  def viewAsString(form: Form[_] = form, radioOptions: Set[RadioOption] = YourSaIsNotInThisAccount.options) =
    yourSaIsNotInThisAccount(
      frontendAppConfig,
      form
    )(HtmlFormat.empty)(fakeRequest, messages).toString

  "Not in this account controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller()().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", YourSaIsNotInThisAccount.options.head.value))

      val result = controller()().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller()().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }
  }
}
