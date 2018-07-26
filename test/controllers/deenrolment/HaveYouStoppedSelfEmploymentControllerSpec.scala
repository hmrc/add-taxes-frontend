/*
 * Copyright 2018 HM Revenue & Customs
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

package controllers.deenrolment

import controllers._
import controllers.actions._
import controllers.deenrolment.routes.StopFilingSelfAssessmentController
import forms.deenrolment.HaveYouStoppedSelfEmploymentFormProvider
import models.deenrolment.HaveYouStoppedSelfEmployment
import play.api.data.Form
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.{FakeNavigator, HmrcEnrolmentType}
import utils.HmrcEnrolmentType.CORP_TAX
import views.html.deenrolment.haveYouStoppedSelfEmployment

class HaveYouStoppedSelfEmploymentControllerSpec extends ControllerSpecBase {

  def onwardRoute = controllers.routes.IndexController.onPageLoad()

  val formProvider = new HaveYouStoppedSelfEmploymentFormProvider()
  val form = formProvider()

  def controller()(enrolmentTypes: HmrcEnrolmentType*) =
    new HaveYouStoppedSelfEmploymentController(
      frontendAppConfig,
      messagesApi,
      new FakeNavigator(desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction(enrolmentTypes: _*),
      formProvider)

  def viewAsString(form: Form[_] = form) =
    haveYouStoppedSelfEmployment(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "HaveYouStoppedSelfEmployment Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller()().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", HaveYouStoppedSelfEmployment.options.head.value))

      val result = controller()().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller()().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return OK" in {
      val result = controller()().onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    for (option <- HaveYouStoppedSelfEmployment.options) {
      s"redirect to next page when '${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value))
        val result = controller()().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "redirect to stop filing self assessment page" when {
      "page is loaded" in {
        val result = controller()(CORP_TAX).onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(StopFilingSelfAssessmentController.onPageLoad().url)
      }

      "page is submitted" in {
        val result = controller()(CORP_TAX).onSubmit()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(StopFilingSelfAssessmentController.onPageLoad().url)
      }
    }
  }
}
