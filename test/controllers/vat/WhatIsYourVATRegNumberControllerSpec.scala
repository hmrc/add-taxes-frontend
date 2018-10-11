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

package controllers.vat

import controllers.ControllerSpecBase
import controllers.actions.{FakeAuthAction, FakeServiceInfoAction}
import forms.vat.WhatIsYourVATRegNumberFormProvider
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import services.VatSubscriptionService
import utils.FakeNavigator
import views.html.vat.whatIsYourVATRegNumber

import scala.concurrent.Future

class WhatIsYourVATRegNumberControllerSpec extends ControllerSpecBase with MockitoSugar {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()

  val formProvider = new WhatIsYourVATRegNumberFormProvider()
  val form = formProvider()
  lazy val mockVatSubscriptionService: VatSubscriptionService = mock[VatSubscriptionService]
  val testVrn = "968501689"

  def controller() =
    new WhatIsYourVATRegNumberController(
      frontendAppConfig,
      messagesApi,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      mockVatSubscriptionService,
      formProvider
    )

  def viewAsString(form: Form[_] = form): String =
    whatIsYourVATRegNumber(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "WhatIsYourVATRegNumber Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {

      when(mockVatSubscriptionService.getMandationStatus(any())(any(), any()))
        .thenReturn(Future.successful(Some(true)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testVrn))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockVatSubscriptionService, times(1)).getMandationStatus(any())(any(), any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }
  }
}
