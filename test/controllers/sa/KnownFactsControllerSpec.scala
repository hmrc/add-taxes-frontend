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
import forms.sa.KnownFactsFormProvider
import models.sa.{KnownFacts, SAUTR}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{KnownFactsFormValidator, UserAnswers}
import views.html.sa.knownFacts

import scala.concurrent.Future

class KnownFactsControllerSpec extends ControllerSpecBase with MockitoSugar {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()

  val view: knownFacts = injector.instanceOf[knownFacts]
  val knownFactsValidator: KnownFactsFormValidator = injector.instanceOf[KnownFactsFormValidator]
  val mockEnrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]

  val formProvider = new KnownFactsFormProvider(knownFactsValidator, frontendAppConfig)
  val form = formProvider()

  def controller() =
    new KnownFactsController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      mockDataCacheConnector,
      mockEnrolmentStoreProxyConnector,
      view)


  def viewAsString(form: Form[KnownFacts] = form): String = {
    injector.instanceOf[knownFacts].apply(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString
  }
  def viewAsString(): String = {
     injector.instanceOf[knownFacts].apply(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString
  }

  "KnownFacts Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return bad request with a invalid nino" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("nino", "zzzzzzzzzzzzz"))
      val boundForm = form.bind(Map("nino" -> "zzzzzzzzzzzzz"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return bad request with a invalid postcode" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "zzzzzzzzzzzzz"))
      val boundForm = form.bind(Map("postcode" -> "zzzzzzzzzzzzz"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return bad request with a invalid postcode and nino" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "zzzzzzzzzzzzz"),("nino", "zzzzzzzzzzzzz"))
      val boundForm = form.bind(Map("postcode" -> "zzzzzzzzzzzzz", "nino" -> "zzzzzzzzzzzzz"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return bad request with a no data" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody()
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
    }

    "redirect to TBC(for now languageController cy) when data cache returns none" in {
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(None))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "AA1 1AA"), ("nino", "AA000000A"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/business-account/add-tax/language/cy")
    }

    "redirect to TBC(for now languageController en) when query known facts returns true" in {
      val saUtr = new SAUTR("1000000000")
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(saUtr)))
      when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any()))
        .thenReturn(Future.successful(true))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "AA1 1AA"), ("nino", "AA000000A"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/business-account/add-tax/language/en")
    }

    "redirect to TBC(for now languageController en) when query known facts returns true and postcode only" in {
      val saUtr = new SAUTR("1000000000")
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(saUtr)))
      when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any()))
        .thenReturn(Future.successful(true))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("postcode", "AA1 1AA"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/business-account/add-tax/language/en")
    }

    "redirect to TBC(for now languageController en) when query known facts returns true and nino only" in {
      val saUtr = new SAUTR("1000000000")
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(saUtr)))
      when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any()))
        .thenReturn(Future.successful(true))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("nino", "AA000000A"))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/business-account/add-tax/language/en")
    }
  }
}
