/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.other.alcohol.awrs

import config.featureToggles.FeatureSwitch.VapingDutySwitch
import controllers.ControllerSpecBase
import forms.other.alcohol.awrs.SelectAlcoholSchemeFormProvider
import models.other.alcohol.awrs.SelectAlcoholScheme
import models.other.alcohol.awrs.SelectAlcoholScheme.{AD, ATWD, AWRS, VPD}
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.{Enrolments, FakeNavigator}
import views.html.other.alcohol.awrs.selectAlcoholScheme

class SelectAlcoholSchemeControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val formProvider                    = new SelectAlcoholSchemeFormProvider()
  val form: Form[SelectAlcoholScheme] = formProvider()

  val view: selectAlcoholScheme = injector.instanceOf[selectAlcoholScheme]

  def controller(): SelectAlcoholSchemeController =
    new SelectAlcoholSchemeController(
      frontendAppConfig,
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      view
    )

  def viewAsString(form: Form[_] = form): String =
    new selectAlcoholScheme(formWithCSRF, mainTemplate)(frontendAppConfig, form, SelectAlcoholScheme.values.toSeq)(HtmlFormat.empty)(
      fakeRequest,
      messages).toString

  "onPageLoad" must {
    "return OK and the correct radio button content in the view" in {
      val result        = controller().onPageLoad()(fakeRequest.withMethod("GET"))
      val contentOutput = contentAsString(result)

      status(result) mustBe OK
      contentOutput mustBe viewAsString()
      contentOutput must include("Alcohol &amp; Tobacco Warehousing Declarations (ATWD)")
      contentOutput must include("Alcohol Wholesaler Registration Scheme (AWRS)")
      contentOutput must include("Alcohol Duty (AD)")
      contentOutput must include("Vaping Products Duty")
    }
  }

  "onSubmit" must {
    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm   = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    for (option <- SelectAlcoholScheme.options)
      s"redirect to next page when '${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value)).withMethod("POST")
        val result      = controller().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
  }

  "getFilteredAlcoholValues" must {
    "return all radio options (ATWD, AWRS, AD and VPD)" when {
      "VapingDutySwitch switch is turned ON and user does not have HMRC-VPD-ORG enrolment" in {
        val result = controller().getFilteredAlcoholValues(requestWithEnrolments(keys = ""), frontendAppConfig)

        result mustBe Seq(ATWD, AWRS, AD, VPD)
      }
    }

    "all but Vaping Products Duty radio options" when {
      "user has HMRC-VPD-ORG enrolment" in {
        val result = controller().getFilteredAlcoholValues(requestWithEnrolments(keys = Enrolments.VPD.identifier), frontendAppConfig)

        result mustBe Seq(ATWD, AWRS, AD)
      }
      "VapingDutySwitch switch is turned OFF" in {
        disable(VapingDutySwitch)
        val result = controller().getFilteredAlcoholValues(requestWithEnrolments(keys = ""), frontendAppConfig)

        result mustBe Seq(ATWD, AWRS, AD)
        enable(VapingDutySwitch)
      }
    }
  }
}
