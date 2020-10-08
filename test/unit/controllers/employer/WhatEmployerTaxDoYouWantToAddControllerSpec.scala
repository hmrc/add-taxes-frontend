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

package controllers.employer

import controllers._
import controllers.actions.FakeServiceInfoAction
import forms.employer.WhatEmployerTaxDoYouWantToAddFormProvider
import models.employer.WhatEmployerTaxDoYouWantToAdd
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.{FakeNavigator, HmrcEnrolmentType, RadioOption}
import views.html.employer.whatEmployerTaxDoYouWantToAdd

class WhatEmployerTaxDoYouWantToAddControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()

  val formProvider = new WhatEmployerTaxDoYouWantToAddFormProvider()
  val form: Form[WhatEmployerTaxDoYouWantToAdd] = formProvider()

  val view: whatEmployerTaxDoYouWantToAdd = injector.instanceOf[whatEmployerTaxDoYouWantToAdd]

  def controller()(enrolmentTypes: HmrcEnrolmentType*): WhatEmployerTaxDoYouWantToAddController = {
    new WhatEmployerTaxDoYouWantToAddController(
      frontendAppConfig,
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction(enrolmentTypes: _*),
      formProvider,
      view
    )
  }

  def viewAsString(form: Form[_] = form, radioOptions: Seq[RadioOption] = WhatEmployerTaxDoYouWantToAdd.options): String =
    new whatEmployerTaxDoYouWantToAdd(formWithCSRF, mainTemplate)(frontendAppConfig, form, radioOptions)(HtmlFormat.empty)(fakeRequest, messages).toString

  "WhatEmployerTaxDoYouWantToAdd Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller()().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", WhatEmployerTaxDoYouWantToAdd.options.head.value))

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

    for (option <- WhatEmployerTaxDoYouWantToAdd.options) {
      s"redirect to next page when '${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value))
        val result = controller()().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "hide EPAYE option" when {
      val radioOptions =
        WhatEmployerTaxDoYouWantToAdd.options.filterNot(_.value == WhatEmployerTaxDoYouWantToAdd.EPAYE.toString)

      "on page load and enrolled for EPAYE" in {
        val result = controller()(HmrcEnrolmentType.EPAYE).onPageLoad()(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions)

        contentAsString(result) mustBe view
      }

      "on page submit and enrolled for EPAYE" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions)

        val result = controller()(HmrcEnrolmentType.EPAYE).onSubmit()(postRequest)

        contentAsString(result) mustBe view
      }
    }

    "hide Pension scheme option" when {
      val radioOptions =
        WhatEmployerTaxDoYouWantToAdd.options.filterNot(_.value == WhatEmployerTaxDoYouWantToAdd.PS.toString)

      "on page load and enrolled for Pension schemes and Administrators" in {
        val result = controller()(HmrcEnrolmentType.PSA, HmrcEnrolmentType.PP).onPageLoad()(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions)

        contentAsString(result) mustBe view
      }

      "on page load and enrolled for Pension schemes" in {
        val result = controller()(HmrcEnrolmentType.PP).onPageLoad()(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions)

        contentAsString(result) mustBe view
      }

      "on page submit and enrolled for Pension schemes" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions)

        val result = controller()(HmrcEnrolmentType.PP).onSubmit()(postRequest)

        contentAsString(result) mustBe view
      }

      "on page submit and enrolled for Pension schemes and Administrators" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions)

        val result = controller()(HmrcEnrolmentType.PSA, HmrcEnrolmentType.PP).onSubmit()(postRequest)

        contentAsString(result) mustBe view
      }
    }
  }
}
