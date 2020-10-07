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

import controllers.Assets.{OK, SEE_OTHER}
import controllers.ControllerSpecBase
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import play.twirl.api.HtmlFormat
import views.html.sa.successfulEnrolment

class EnrolmentSuccessControllerSpec extends ControllerSpecBase with MockitoSugar {

  val view: successfulEnrolment = injector.instanceOf[successfulEnrolment]

  def controller(pinInPostFeature: Boolean = true): EnrolmentSuccessController = {
    new EnrolmentSuccessController(
      FakeAuthAction,
      frontendAppConfig,
      FakeServiceInfoAction,
      mcc,
      view
    ){
      override val pinAndPostFeatureToggle: Boolean = pinInPostFeature
    }
  }

  def viewAsString(): String =
    new successfulEnrolment(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "EnrolmentSuccess Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to BTA home page when the toggle is set to false" in {
      val result = controller(pinInPostFeature = false).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(frontendAppConfig.getBusinessAccountUrl("home"))
    }
  }
}
