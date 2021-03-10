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
import controllers.Assets.{OK, SEE_OTHER}
import controllers.ControllerSpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import play.twirl.api.HtmlFormat
import views.html.sa.successfulEnrolment

import scala.concurrent.Future

class EnrolmentSuccessControllerSpec extends ControllerSpecBase with MockitoSugar {

  val view: successfulEnrolment = injector.instanceOf[successfulEnrolment]
  val btaOrigin = "bta-sa"

  def controller(): EnrolmentSuccessController = {
    new EnrolmentSuccessController(
      FakeAuthAction,
      frontendAppConfig,
      FakeServiceInfoAction,
      mcc,
      view
    )
  }

  def viewAsString(origin: String, returnUrl: Option[String] = None): String =
    new successfulEnrolment(mainTemplate)(frontendAppConfig, origin)(HtmlFormat.empty)(fakeRequest, messages).toString

  "EnrolmentSuccess Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(btaOrigin)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(btaOrigin)
    }
  }
}
