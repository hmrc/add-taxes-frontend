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
import views.html.sa.signInDifferent


class SignInDifferentControllerSpec extends ControllerSpecBase with MockitoSugar {

  val view: signInDifferent = injector.instanceOf[signInDifferent]
  val mockDataCacheConnector = mock[DataCacheConnector]
  val btaOrigin: String = "bta-sa"

  def controller(): SignInDifferentController = {
    new SignInDifferentController(
      frontendAppConfig,
      FakeAuthAction,
      FakeServiceInfoAction,
      mockDataCacheConnector,
      mcc,
      view
    )
  }

  def viewAsString(origin: String): String =
    new signInDifferent(mainTemplate)(frontendAppConfig, origin)(HtmlFormat.empty)(fakeRequest, messages).toString



  "TryPinInPost Controller" must {
    "return OK and the correct view for a GET when feature toggle is set to true" in {
      val result = controller().onPageLoad(btaOrigin)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(btaOrigin)
    }


  }

}
