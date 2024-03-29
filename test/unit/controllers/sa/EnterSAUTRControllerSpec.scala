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
import controllers.ControllerSpecBase
import forms.sa.SAUTRFormProvider
import models.sa.SAUTR
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.Form
import play.api.mvc.Call
import play.api.mvc.Results.Redirect
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import service.{AuditService, SelectSaCategoryService}
import views.html.sa.enterSAUTR

import scala.concurrent.Future

class EnterSAUTRControllerSpec extends PlaySpec with ControllerSpecBase with MockitoSugar {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val formProvider = new SAUTRFormProvider()
  val form: Form[SAUTR] = formProvider()
  val btaOrigin: String = "bta-sa"
  val mockEnrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockAuditService: AuditService = mock[AuditService]
  val mockSaCategoryService: SelectSaCategoryService = mock[SelectSaCategoryService]

  val view: enterSAUTR = injector.instanceOf[enterSAUTR]

  def verifyDataCacheSave(expectedTimes: Int): Unit =
    verify(mockDataCacheConnector, times(expectedTimes)).save(any(), any(), any())(any())

  def controller(): EnterSAUTRController = {
    new EnterSAUTRController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      mockDataCacheConnector,
      view,
      mockSaCategoryService
    )
  }

  def viewAsString(origin: String): String =
    new enterSAUTR(formWithCSRF, mainTemplate)(frontendAppConfig, form, origin)(HtmlFormat.empty)(fakeRequest, messages).toString

  def viewAsString(form: Form[SAUTR] = form, origin: String): String =
    new enterSAUTR(formWithCSRF, mainTemplate)(frontendAppConfig, form, origin)(HtmlFormat.empty)(fakeRequest, messages).toString

  "EnterSAUTR Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(Some(btaOrigin))(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(btaOrigin)
    }

    "return OK and the correct view for a GET with a default" in {
      val result = controller().onPageLoad(None)(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(btaOrigin)
    }

    "return bad request when invalid sa utr is provided" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid sa utr")).withMethod("POST")
      val boundForm = form.bind(Map("value" -> "invalid sa utr"))

      val result = controller().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe BAD_REQUEST
      verifyDataCacheSave(expectedTimes = 0)
      contentAsString(result) mustBe viewAsString(boundForm, btaOrigin)
    }

    "redirect to the describes you page when try again is false" in {
      when(mockDataCacheConnector.getEntry[Boolean](any(), any())(any())).thenReturn(Future.successful(Some(false)))
      when(mockDataCacheConnector.save[SAUTR](any(), any(), any())(any())).thenReturn(Future.successful(emptyCacheMap))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "0123456789")).withMethod("POST")

      val result = controller().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe SEE_OTHER
      verifyDataCacheSave(expectedTimes = 1)
      redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment?origin=bta-sa")
    }

    "redirect when valid sa utr is submitted and is in the enrolment store and " in {
      when(mockDataCacheConnector.save[SAUTR](any(), any(), any())(any())).thenReturn(Future.successful(emptyCacheMap))
      when(mockDataCacheConnector.getEntry[Boolean](any(),any())(any())).thenReturn(Future.successful(Some(true)))
      when(mockSaCategoryService.saCategoryResult(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(onwardRoute.url)))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "0123456789")).withMethod("POST")

      val result = controller().onSubmit(btaOrigin)(postRequest)

      status(result) mustBe SEE_OTHER
      verifyDataCacheSave(expectedTimes = 2)
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }
  }

}
