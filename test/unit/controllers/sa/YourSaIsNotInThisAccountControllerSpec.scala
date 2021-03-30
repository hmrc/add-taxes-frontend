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

package controllers.sa

import controllers.ControllerSpecBase
import forms.sa.YourSaIsNotInThisAccountFormProvider
import models.sa.YourSaIsNotInThisAccount
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers.{contentAsString, redirectLocation, status, _}
import play.twirl.api.HtmlFormat
import service.SaService
import utils.HmrcEnrolmentType
import views.html.sa.yourSaIsNotInThisAccount

class YourSaIsNotInThisAccountControllerSpec extends ControllerSpecBase with MockitoSugar {
  val btaOrigin: String = "bta-sa"
  def onwardRoute: Call = controllers.sa.routes.SelectSACategoryController.onPageLoadHasUTR(Some(btaOrigin))

  val formProvider = new YourSaIsNotInThisAccountFormProvider()
  val form: Form[YourSaIsNotInThisAccount] = formProvider()
  val view: yourSaIsNotInThisAccount = injector.instanceOf[yourSaIsNotInThisAccount]
  val mockSaService: SaService = app.injector.instanceOf[SaService]

  def controller()(enrolmentTypes: HmrcEnrolmentType*): YourSaIsNotInThisAccountController = {
    new YourSaIsNotInThisAccountController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction(enrolmentTypes: _*),
      formProvider,
      view,
      mockSaService
    )
  }

  def viewAsString(form: Form[_] = form, origin: String): String =
    new yourSaIsNotInThisAccount(formWithCSRF, mainTemplate)(frontendAppConfig, form, origin)(HtmlFormat.empty)(fakeRequest, messages).toString

  "Not in this account controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller()().onPageLoad(btaOrigin)(fakeRequest.withSession(("tryingToAccessSa", "true")))

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(origin = btaOrigin)
    }

    "redirect to the BTA homepage when the expected session flag is missing" in {
      val result = controller()().onPageLoad(btaOrigin)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("http://localhost:9020/business-account")
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", YourSaIsNotInThisAccount.options.head.value))

      val result = controller()().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller()().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm, btaOrigin)
    }
  }
}
