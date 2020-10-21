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

package controllers.vat

import controllers._
import controllers.actions._
import forms.vat.AgriculturalFlatRateSchemeFormProvider
import models.vat.AgriculturalFlatRateScheme
import org.scalatest.BeforeAndAfterEach
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import playconfig.featuretoggle.FeatureToggleSupport
import utils.FakeNavigator
import views.html.vat.agriculturalFlatRateScheme

class AgriculturalFlatRateSchemeControllerSpec extends ControllerSpecBase with BeforeAndAfterEach with FeatureToggleSupport {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()

  val formProvider = new AgriculturalFlatRateSchemeFormProvider()
  val form: Form[AgriculturalFlatRateScheme] = formProvider()

  val view: agriculturalFlatRateScheme = injector.instanceOf[agriculturalFlatRateScheme]

  def controller(): AgriculturalFlatRateSchemeController = {
    new AgriculturalFlatRateSchemeController(
      frontendAppConfig,
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      featureDepandantAction = app.injector.instanceOf[FeatureDependantAction],
      view
    )
  }

  def viewAsString(form: Form[_] = form): String =
    new agriculturalFlatRateScheme(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  "AgriculturalFlatRateScheme Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", AgriculturalFlatRateScheme.options.head.value))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return OK" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    for (option <- AgriculturalFlatRateScheme.options) {
      s"redirect to next page when '${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value))
        val result = controller().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }
  }
}
