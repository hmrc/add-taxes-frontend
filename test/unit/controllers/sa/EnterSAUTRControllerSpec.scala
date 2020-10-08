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

import connectors.{DataCacheConnector, EnrolmentStoreProxyConnector}
import controllers._
import forms.sa.SAUTRFormProvider
import models.sa.SAUTR
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import playconfig.featuretoggle.{FeatureToggleSupport, PinAndPostFeature}
import utils.FakeNavigator
import views.html.sa.enterSAUTR

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnterSAUTRControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()

  val formProvider = new SAUTRFormProvider()

  val form: Form[SAUTR] = formProvider()

  val mockEnrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]

  val view: enterSAUTR = injector.instanceOf[enterSAUTR]

  def verifyDataCacheSave(expectedTimes: Int): Unit =
    verify(mockDataCacheConnector, times(expectedTimes)).save(any(), any(), any())(any())

  def controller(pinAndPostToggle: Boolean = false): EnterSAUTRController = {
    new EnterSAUTRController(
      frontendAppConfig,
      mcc,
      new FakeNavigator(desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      mockEnrolmentStoreProxyConnector,
      mockDataCacheConnector,
      view
    ){
      override val pinAndPostFeatureToggle: Boolean = pinAndPostToggle
    }
  }

  def viewAsString(): String =
    new enterSAUTR(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  def viewAsString(form: Form[SAUTR] = form): String =
    new enterSAUTR(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "EnterSAUTR Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return bad request when invalid sa utr is provided" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid sa utr"))
      val boundForm = form.bind(Map("value" -> "invalid sa utr"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      verifyDataCacheSave(expectedTimes = 0)
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect when valid sa utr is submitted and is in the enrolment store" in {
      when(mockEnrolmentStoreProxyConnector.checkExistingUTR(any())(any(), any())).thenReturn(Future.successful(true))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "0123456789"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      verifyDataCacheSave(expectedTimes = 0)
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect when valid sa utr is submitted and is in the enrolment store and pin and post feature is on" in {
      when(mockDataCacheConnector.save[SAUTR](any(), any(), any())(any())).thenReturn(Future.successful(emptyCacheMap))
      when(mockEnrolmentStoreProxyConnector.checkExistingUTR(any())(any(), any())).thenReturn(Future.successful(true))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "0123456789"))

      val result = controller(pinAndPostToggle = true).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      verifyDataCacheSave(expectedTimes = 1)
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }
  }

}
