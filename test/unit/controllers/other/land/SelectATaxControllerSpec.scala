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

package controllers.other.land

import controllers._
import forms.other.land.SelectATaxFormProvider
import models.other.land.SelectATax
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.{FakeNavigator, HmrcEnrolmentType, RadioOption}
import views.html.other.land.selectATax

class SelectATaxControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val formProvider = new SelectATaxFormProvider()
  val form: Form[SelectATax] = formProvider()

  val view: selectATax = injector.instanceOf[selectATax]

  def controller()(enrolments: HmrcEnrolmentType*) =
    new SelectATaxController(
      frontendAppConfig,
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction(enrolments: _*),
      formProvider,
      view
    )

  def viewAsString(form: Form[_] = form, radioOptions: Set[RadioOption] = SelectATax.options): String =
    new selectATax(formWithCSRF, mainTemplate)(frontendAppConfig, form, radioOptions)(HtmlFormat.empty)(fakeRequest, messages).toString

  "SelectATax Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller()().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", SelectATax.options.head.value))

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

    "return OK" in {
      val result = controller()().onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    for (option <- SelectATax.options) {
      s"redirect to next page when '${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value))
        val result = controller()().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "not display sdlt radio option" when {
      val radioOptions = SelectATax.options.filterNot(_.value == "SDLT")

      "page is loaded and sdlt is enrolled" in {
        val result = controller()(HmrcEnrolmentType.SDLT).onPageLoad()(fakeRequest)

        contentAsString(result) mustBe viewAsString(radioOptions = radioOptions)
      }

      "page errors and sdlt is enrolled" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = controller()(HmrcEnrolmentType.SDLT).onSubmit()(postRequest)

        contentAsString(result) mustBe viewAsString(boundForm, radioOptions)
      }
    }

  }
}
