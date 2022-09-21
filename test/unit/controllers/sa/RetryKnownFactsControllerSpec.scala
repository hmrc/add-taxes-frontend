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

import connectors.DataCacheConnector
import controllers.ControllerSpecBase
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.sa.retryKnownFacts

import scala.concurrent.ExecutionContext.Implicits.global


class RetryKnownFactsControllerSpec extends ControllerSpecBase with MockitoSugar {

  val view: retryKnownFacts = injector.instanceOf[retryKnownFacts]
  val mockDataCacheConnector = mock[DataCacheConnector]
  val btaOrigin: String = "bta-sa"
  val ninoExists: Boolean = true

  def controller(): RetryKnownFactsController = {
    new RetryKnownFactsController(
      frontendAppConfig,
      FakeAuthAction,
      FakeServiceInfoAction,
      mockDataCacheConnector,
      mcc,
      view
    )
  }

  def viewAsString(origin: String): String =
    new retryKnownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, origin, ninoExists)(HtmlFormat.empty)(fakeRequest, messages).toString

  if(ninoExists) {

    "TryPinInPost Controller" must {
      "return OK and the correct view for a GET when feature toggle is set to true with nino" in {
        val result = controller().onPageLoad(btaOrigin)(fakeRequest)

        status(result) mustBe OK
      }
    }

  } else {

  "TryPinInPost Controller" must {
    "return OK and the correct view for a GET when feature toggle is set to true" in {
      val result = controller().onPageLoad(btaOrigin)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(btaOrigin)
    }

  }

    "redirect to enter SaUTR page" in {
      val result = controller().onSubmit(btaOrigin)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).get mustBe routes.EnterSAUTRController.onPageLoad(Some(btaOrigin)).url
    }
  }

}
