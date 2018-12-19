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

package controllers.sa

import controllers._
import controllers.actions.{FakeServiceInfoAction, _}
import forms.sa.SelectSACategoryFormProvider
import models.sa.SelectSACategory
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.{FakeNavigator, HmrcEnrolmentType, RadioOption}
import views.html.sa.selectSACategory

class SelectSACategoryControllerSpec extends ControllerSpecBase {

  def onwardRoute = controllers.routes.IndexController.onPageLoad()

  val formProvider = new SelectSACategoryFormProvider()
  val form = formProvider()

  def controller()(enrolmentTypes: HmrcEnrolmentType*) =
    new SelectSACategoryController(
      frontendAppConfig,
      messagesApi,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction(enrolmentTypes: _*),
      formProvider
    )

  def viewAsString(form: Form[_] = form, radioOptions: Set[RadioOption] = SelectSACategory.options) =
    selectSACategory(
      frontendAppConfig,
      form,
      routes.SelectSACategoryController.onSubmitHasUTR(),
      radioOptions
    )(HtmlFormat.empty)(fakeRequest, messages).toString

  def viewAsStringNoUTR(form: Form[_] = form, radioOptions: Set[RadioOption] = SelectSACategory.options) =
    selectSACategory(
      frontendAppConfig,
      form,
      routes.SelectSACategoryController.onSubmitNoUTR(),
      radioOptions
    )(HtmlFormat.empty)(fakeRequest, messages).toString

  "SelectSACategory Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller()().onPageLoadHasUTR()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return OK and the correct view for a GET when called on the no UTR path" in {
      val result = controller()().onPageLoadNoUTR()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsStringNoUTR()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", SelectSACategory.options.head.value))

      val result = controller()().onSubmitHasUTR()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller()().onSubmitHasUTR()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return OK" in {
      val result = controller()().onPageLoadHasUTR()(fakeRequest)

      status(result) mustBe OK
    }

    for (option <- SelectSACategory.options) {
      s"redirect to next page when '${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", (option.value)))
        val result = controller()().onSubmitHasUTR()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "show all options" when {
      val radioOptions: Set[RadioOption] = SelectSACategory.options

      "on page load and not enrolled for SA or Trust" in {
        val result = controller()().onPageLoadHasUTR()(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions)

        contentAsString(result) mustBe view
      }

      "on page submit and not enrolled for SA or Trust" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions)

        val result = controller()().onSubmitHasUTR()(postRequest)

        contentAsString(result) mustBe view
      }
    }

    "hide 'Individual or sole trader' option" when {
      val radioOptions: Set[RadioOption] =
        SelectSACategory.options.filterNot(_.value == SelectSACategory.Sa.toString)

      "on page load and enrolled for SA" in {
        val result = controller()(HmrcEnrolmentType.SA).onPageLoadHasUTR()(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions)

        contentAsString(result) mustBe view
      }

      "on page submit and enrolled for SA" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions)

        val result = controller()(HmrcEnrolmentType.SA).onSubmitHasUTR()(postRequest)

        contentAsString(result) mustBe view
      }
    }

    "hide 'Trust' option" when {
      val radioOptions: Set[RadioOption] =
        SelectSACategory.options.filterNot(_.value == SelectSACategory.Trust.toString)

      "on page load and enrolled for Trust" in {
        val result = controller()(HmrcEnrolmentType.RegisterTrusts).onPageLoadHasUTR()(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions)

        contentAsString(result) mustBe view
      }

      "on page submit and enrolled for Trust" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions)

        val result = controller()(HmrcEnrolmentType.RegisterTrusts).onSubmitHasUTR()(postRequest)

        contentAsString(result) mustBe view
      }
    }

    "redirect to do you want to add a partner" when {
      "on page load and enrolled for SA and Trust" in {
        val result =
          controller()(HmrcEnrolmentType.SA, HmrcEnrolmentType.RegisterTrusts).onPageLoadHasUTR()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")
      }

      "on submit and enrolled for SA and Trust" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val result = controller()(HmrcEnrolmentType.SA, HmrcEnrolmentType.RegisterTrusts).onSubmitHasUTR()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")

      }
    }
  }
}
