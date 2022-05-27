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

import config.featureToggles.FeatureToggleSupport
import controllers._
import controllers.sa.{routes => saRoutes}
import forms.sa.KnownFactsPostcodeFormProvider
import models.sa.KnownFactsPostcode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.{Form, FormError}
import play.api.mvc.Call
import play.api.mvc.Results.Redirect
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import service.KnownFactsService
import utils.KnownFactsFormValidator
import views.html.sa.postcodeKnownFacts

import scala.concurrent.Future

class PostCodeControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val view: postcodeKnownFacts = injector.instanceOf[postcodeKnownFacts]
  val knownFactsValidator: KnownFactsFormValidator = injector.instanceOf[KnownFactsFormValidator]
  val mockKnownFactsService: KnownFactsService = mock[KnownFactsService]

  val formProvider = new KnownFactsPostcodeFormProvider(knownFactsValidator, frontendAppConfig)
  val form: Form[KnownFactsPostcode] = formProvider()
  val btaOrigin: String = "bta-sa"

  def controller(): PostcodeController = {
    new PostcodeController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      view,
      mockKnownFactsService)
  }


  def viewAsString(form: Form[KnownFactsPostcode] = form, origin: String): String = {
    injector.instanceOf[postcodeKnownFacts].apply(frontendAppConfig, form, origin)(HtmlFormat.empty)(fakeRequest, messages).toString
  }
  def viewAsString(origin: String): String = {
     injector.instanceOf[postcodeKnownFacts].apply(frontendAppConfig, form, origin)(HtmlFormat.empty)(fakeRequest, messages).toString
  }

  "Postcode  Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(btaOrigin)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(btaOrigin)
    }

    "return bad request with a invalid postcode" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "zzzzzzzzzzzzz"))
      val boundForm = form.bind(Map("postcode" -> "zzzzzzzzzzzzz"))
      val result = controller().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm, btaOrigin)
    }

    "return bad request if postcode and isAbroad provided" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "AA1 1AA"), ("isAbroad", "Y"))
      val result = controller().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(form.withError(FormError("postcode", "enterKnownFacts.postcode.error.required")).withError(FormError("isAbroad", "enterKnownFacts.postcode.error.required")), btaOrigin)
    }


    "return bad request with a no data" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody()
      val result = controller().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe BAD_REQUEST
    }

    "redirect to Try Pin and Post when that redirect is returned from service" in {
      when(mockKnownFactsService.knownFactsLocation(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(saRoutes.RetryKnownFactsController.onPageLoad(btaOrigin))))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "AA1 1AA"))
      val result = controller().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.RetryKnownFactsController.onPageLoad(btaOrigin).url)
    }

    "redirect to successful enrolment page when query known facts returns true on postcode" in {
      when(mockKnownFactsService.knownFactsLocation(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(saRoutes.EnrolmentSuccessController.onPageLoad(btaOrigin))))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "AA1 1AA"))
      val result = controller().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.EnrolmentSuccessController.onPageLoad(btaOrigin).url)
    }

    "redirect to successful enrolment page when query known facts returns true on isAbroad" in {
      when(mockKnownFactsService.knownFactsLocation(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(saRoutes.EnrolmentSuccessController.onPageLoad(btaOrigin))))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("isAbroad", "Y"))
      val result = controller().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.EnrolmentSuccessController.onPageLoad(btaOrigin).url)
    }

  }
}
