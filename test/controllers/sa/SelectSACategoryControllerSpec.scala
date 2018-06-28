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

import play.api.data.Form
import play.api.libs.json.JsString
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, HmrcEnrolmentType, RadioOption}
import controllers.actions.{FakeServiceInfoAction, _}
import controllers._
import play.api.test.Helpers._
import forms.sa.SelectSACategoryFormProvider
import identifiers.SelectSACategoryId
import models.sa.SelectSACategory
import play.twirl.api.HtmlFormat
import views.html.sa.selectSACategory

class SelectSACategoryControllerSpec extends ControllerSpecBase {

  def onwardRoute = controllers.routes.IndexController.onPageLoad()

  val formProvider = new SelectSACategoryFormProvider()
  val form = formProvider()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap)(enrolmentTypes: HmrcEnrolmentType*) =
    new SelectSACategoryController(
      frontendAppConfig,
      messagesApi,
      new FakeNavigator(desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction(enrolmentTypes: _*),
      formProvider
    )

  def viewAsString(form: Form[_] = form, radioOptions: Set[RadioOption] = SelectSACategory.options) =
    selectSACategory(frontendAppConfig, form, radioOptions)(HtmlFormat.empty)(fakeRequest, messages).toString

  "SelectSACategory Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller()().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", SelectSACategory.options.head.value))

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

    "return OK if no existing data is found" in {
      val result = controller(dontGetAnyData)().onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    for (option <- SelectSACategory.options) {
      s"redirect to next page when '${option.value}' is submitted and no existing data is found" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", (option.value)))
        val result = controller(dontGetAnyData)().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "show all options" when {
      val radioOptions: Set[RadioOption] = SelectSACategory.options

      "on page load and not enrolled for SA or Trust" in {
        val result = controller()().onPageLoad()(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions)

        contentAsString(result) mustBe view
      }

      "on page submit and not enrolled for SA or Trust" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions)

        val result = controller()().onSubmit()(postRequest)

        contentAsString(result) mustBe view
      }
    }

    "hide 'Individual or sole trader' option" when {
      val radioOptions: Set[RadioOption] =
        SelectSACategory.options.filterNot(_.value == SelectSACategory.Sa.toString)

      "on page load and enrolled for SA" in {
        val result = controller()(HmrcEnrolmentType.SA).onPageLoad()(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions)

        contentAsString(result) mustBe view
      }

      "on page submit and enrolled for SA" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions)

        val result = controller()(HmrcEnrolmentType.SA).onSubmit()(postRequest)

        contentAsString(result) mustBe view
      }
    }

    "hide 'Trust' option" when {
      val radioOptions: Set[RadioOption] =
        SelectSACategory.options.filterNot(_.value == SelectSACategory.Trust.toString)

      "on page load and enrolled for Trust" in {
        val result = controller()(HmrcEnrolmentType.RegisterTrusts).onPageLoad()(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions)

        contentAsString(result) mustBe view
      }

      "on page submit and enrolled for Trust" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions)

        val result = controller()(HmrcEnrolmentType.RegisterTrusts).onSubmit()(postRequest)

        contentAsString(result) mustBe view
      }
    }

    "show only 'partnership' option" when {
      val radioOptions: Set[RadioOption] =
        SelectSACategory.options.filter(_.value == SelectSACategory.Partnership.toString)

      "on page load and enrolled for SA and Trust" in {
        val result = controller()(HmrcEnrolmentType.SA, HmrcEnrolmentType.RegisterTrusts).onPageLoad()(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions)

        contentAsString(result) mustBe view
      }

      "on page submit and enrolled for SA and Trust" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions)

        val result = controller()(HmrcEnrolmentType.SA, HmrcEnrolmentType.RegisterTrusts).onSubmit()(postRequest)

        contentAsString(result) mustBe view
      }
    }

  }
}
