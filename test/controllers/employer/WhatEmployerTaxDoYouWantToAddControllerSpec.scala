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

package controllers.employer

import play.api.data.Form
import play.api.libs.json.JsString
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, HmrcEnrolmentType, RadioOption}
import connectors.FakeDataCacheConnector
import controllers.actions.{FakeServiceInfoAction, _}
import controllers._
import play.api.test.Helpers._
import forms.employer.WhatEmployerTaxDoYouWantToAddFormProvider
import identifiers.WhatEmployerTaxDoYouWantToAddId
import models.employer.WhatEmployerTaxDoYouWantToAdd
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.Enrolments
import views.html.employer.whatEmployerTaxDoYouWantToAdd

class WhatEmployerTaxDoYouWantToAddControllerSpec extends ControllerSpecBase {

  def onwardRoute = controllers.routes.IndexController.onPageLoad()

  val formProvider = new WhatEmployerTaxDoYouWantToAddFormProvider()
  val form = formProvider()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap)(enrolmentTypes: HmrcEnrolmentType*) =
    new WhatEmployerTaxDoYouWantToAddController(
      frontendAppConfig,
      messagesApi,
      FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction(enrolmentTypes: _*),
      formProvider
    )

  def viewAsString(form: Form[_] = form, radioOptions: Seq[RadioOption] = WhatEmployerTaxDoYouWantToAdd.options) =
    whatEmployerTaxDoYouWantToAdd(frontendAppConfig, form, radioOptions)(HtmlFormat.empty)(fakeRequest, messages).toString

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

    "return OK if no existing data is found" in {
      val result = controller(dontGetAnyData)().onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    for (option <- WhatEmployerTaxDoYouWantToAdd.options) {
      s"redirect to next page when '${option.value}' is submitted and no existing data is found" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", (option.value)))
        val result = controller(dontGetAnyData)().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "hide EPAYE option" when {
      val radioOptions = WhatEmployerTaxDoYouWantToAdd.optionsWithEpaye

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
  }
}
