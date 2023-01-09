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

package controllers.sa.partnership

import controllers.ControllerSpecBase
import forms.sa.partnership.HaveYouRegisteredPartnershipFormProvider
import models.sa.partnership.HaveYouRegisteredPartnership
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.{Html, HtmlFormat}
import utils.FakeNavigator
import views.html.sa.partnership.haveYouRegisteredPartnership

class HaveYouRegisteredPartnershipControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val formProvider = new HaveYouRegisteredPartnershipFormProvider()
  val form: Form[HaveYouRegisteredPartnership] = formProvider()

  val view: haveYouRegisteredPartnership = injector.instanceOf[haveYouRegisteredPartnership]
  val serviceInfoContent: Html = HtmlFormat.empty
  
  def applyView(form: Form[HaveYouRegisteredPartnership] = form,
                saBoolean: Boolean = false): HtmlFormat.Appendable =
    view.apply(frontendAppConfig, saBoolean, form)(serviceInfoContent)(fakeRequest, messages)

  val saBoolean: Boolean = false

  def controller(): HaveYouRegisteredPartnershipController = {
    new HaveYouRegisteredPartnershipController(
      frontendAppConfig,
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      view
    )
  }

  "HaveYouRegisteredPartnership Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
      contentAsString(result) mustBe applyView().toString
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", HaveYouRegisteredPartnership.options.head.value)).withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe applyView(boundForm).toString()
    }

    "return OK" in {
      val result = controller().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
    }

    for (option <- HaveYouRegisteredPartnership.options) {
      s"redirect to next page when '${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value)).withMethod("POST")
        val result = controller().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }
  }
}
