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

import controllers.Assets.Redirect
import controllers._
import controllers.sa.{routes => saRoutes}
import forms.sa.KnownFactsFormProvider
import models.sa.KnownFacts
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import playconfig.featuretoggle.FeatureToggleSupport
import service.KnownFactsService
import utils.KnownFactsFormValidator
import views.html.sa.postcodeKnownFacts

import scala.concurrent.Future

class PostCodeControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()

  val view: postcodeKnownFacts = injector.instanceOf[postcodeKnownFacts]
  val knownFactsValidator: KnownFactsFormValidator = injector.instanceOf[KnownFactsFormValidator]
  val mockKnownFactsService: KnownFactsService = mock[KnownFactsService]

  val formProvider = new KnownFactsFormProvider(knownFactsValidator, frontendAppConfig)
  val form = formProvider(true)

  def controller(pinAndPostToggle: Boolean = true): PostcodeController = {
    new PostcodeController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      view,
      mockKnownFactsService){
      override val pinAndPostFeatureToggle: Boolean = pinAndPostToggle
    }
  }


  def viewAsString(form: Form[KnownFacts] = form): String = {
    injector.instanceOf[postcodeKnownFacts].apply(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString
  }
  def viewAsString(): String = {
     injector.instanceOf[postcodeKnownFacts].apply(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString
  }

  "Postcode  Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to BTA home page if feature toggle is false" in {
      val result = controller(false).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(frontendAppConfig.getBusinessAccountUrl("home"))
    }

    "return bad request with a invalid postcode" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "zzzzzzzzzzzzz"))
      val boundForm = form.bind(Map("postcode" -> "zzzzzzzzzzzzz"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return bad request if postcode and isAbroad provided" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "AA1 1AA"), ("isAbroad", "Y"))
      val boundForm = form.bind(Map("postcode" -> "AA1 1AA", "isAbroad" -> "Y"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }


    "return bad request with a no data" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody()
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
    }

    "redirect to Try Pin and Post when that redirect is returned from service" in {
      when(mockKnownFactsService.knownFactsLocation(any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(saRoutes.RetryKnownFactsController.onPageLoad())))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "AA1 1AA"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.RetryKnownFactsController.onPageLoad().url)
    }

    "redirect to successful enrolment page when query known facts returns true on postcode" in {
      when(mockKnownFactsService.knownFactsLocation(any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(saRoutes.EnrolmentSuccessController.onPageLoad())))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "AA1 1AA"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.EnrolmentSuccessController.onPageLoad().url)
    }

    "redirect to successful enrolment page when query known facts returns true on isAbroad" in {
      when(mockKnownFactsService.knownFactsLocation(any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(saRoutes.EnrolmentSuccessController.onPageLoad())))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("isAbroad", "Y"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.EnrolmentSuccessController.onPageLoad().url)
    }

  }
}
