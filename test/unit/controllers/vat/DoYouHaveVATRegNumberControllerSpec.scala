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

package controllers.vat

import controllers._
import forms.vat.DoYouHaveVATRegNumberFormProvider
import models.vat.DoYouHaveVATRegNumber
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.FakeNavigator
import views.html.vat.doYouHaveVATRegNumber

class DoYouHaveVATRegNumberControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()

  val formProvider = new DoYouHaveVATRegNumberFormProvider()
  val form: Form[DoYouHaveVATRegNumber] = formProvider()

  val view: doYouHaveVATRegNumber = injector.instanceOf[doYouHaveVATRegNumber]

  def controller(mtdSwitch: Boolean = false): DoYouHaveVATRegNumberController = {
    new DoYouHaveVATRegNumberController(
      frontendAppConfig,
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      view
    ){
      override val useMtdVatReg: Boolean = mtdSwitch
    }
  }

  def viewAsString(form: Form[_] = form): String =
    new doYouHaveVATRegNumber(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "DoYouHaveVATRegNumber Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return OK" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    for (option <- DoYouHaveVATRegNumber.options) {
      s"redirect to next page when '${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value))
        val result = controller().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouHaveVATRegNumber.options.head.value))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to MTD when feature flag is enabled and the user says NO" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouHaveVATRegNumber.options.last.value))

      val result = controller(mtdSwitch = true).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("http://localhost:9895/register-for-vat")
    }

    "not redirect to MTD when feature flag is disabled and the user says NO" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouHaveVATRegNumber.options.last.value))

      val result = controller(mtdSwitch = false).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "not redirect to MTD when feature flag is enabled and the user says YES" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouHaveVATRegNumber.options.head.value))

      val result = controller(mtdSwitch = true).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }
  }
}