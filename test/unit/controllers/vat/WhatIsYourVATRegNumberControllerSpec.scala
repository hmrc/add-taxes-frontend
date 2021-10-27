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

import connectors.VatSubscriptionConnector
import controllers.ControllerSpecBase
import forms.vat.WhatIsYourVATRegNumberFormProvider
import handlers.ErrorHandler
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.FakeNavigator
import views.html.vat.{vatAccountUnavailable, vatRegistrationException, whatIsYourVATRegNumber}

import scala.concurrent.Future


class WhatIsYourVATRegNumberControllerSpec extends ControllerSpecBase with MockitoSugar {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad
  def vatAccountUnavailableRoute: Call = routes.WhatIsYourVATRegNumberController.onPageLoadVatUnanavailable()

  val formProvider = new WhatIsYourVATRegNumberFormProvider()
  val form: Form[String] = formProvider()
  lazy val mockVatSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]

  val testVrn = "968501689"
  val vatAccountUnavailable: vatAccountUnavailable = injector.instanceOf[vatAccountUnavailable]
  val whatIsYourVATRegNumber: whatIsYourVATRegNumber = injector.instanceOf[whatIsYourVATRegNumber]
  val vatRegistrationException: vatRegistrationException = injector.instanceOf[vatRegistrationException]
  val view: whatIsYourVATRegNumber = injector.instanceOf[whatIsYourVATRegNumber]
  val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  def controller(): WhatIsYourVATRegNumberController = {
    new WhatIsYourVATRegNumberController(
      frontendAppConfig,
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      mockVatSubscriptionConnector,
      formProvider,
      vatRegistrationException,
      vatAccountUnavailable,
      errorHandler = errorHandler,
      view
    )
  }

  def viewAsString(form: Form[_] = form): String =
    new whatIsYourVATRegNumber(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "WhatIsYourVATRegNumber Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {

      when(mockVatSubscriptionConnector.getMandationStatus(any())(any(), any()))
        .thenReturn(Future.successful(OK))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testVrn))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockVatSubscriptionConnector, times(1)).getMandationStatus(any())(any(), any())
    }

    "redirect to the vatAccountUnavailable page when migrating data is submitted" in {

      when(mockVatSubscriptionConnector.getMandationStatus(any())(any(), any()))
        .thenReturn(Future.successful(PRECONDITION_FAILED))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testVrn))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(vatAccountUnavailableRoute.toString)
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
