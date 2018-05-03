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

package controllers.other.importexports.ncts

import connectors.FakeDataCacheConnector
import controllers.ControllerSpecBase
import controllers.actions.{FakeServiceInfoAction, _}
import controllers.routes._
import forms.other.importexports.DoYouHaveEORINumberFormProvider
import models.other.importexports.DoYouHaveEORINumber
import play.api.data.Form
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.FakeNavigator
import viewmodels.ViewState
import views.html.other.importexports.doYouHaveEORINumber


class DoYouHaveEORINumberControllerSpec extends ControllerSpecBase {

  def onwardRoute = IndexController.onPageLoad()

  val formProvider = new DoYouHaveEORINumberFormProvider()
  val form = formProvider()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new DoYouHaveEORINumberController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute), FakeAuthAction,
      FakeServiceInfoAction, formProvider)

  def viewAsString(form: Form[_] = form) = doYouHaveEORINumber(frontendAppConfig, form, ViewState(controllers.other.importexports.ncts.routes.DoYouHaveEORINumberController.onSubmit(), "AddNCTSTax"))(HtmlFormat.empty)(fakeRequest, messages).toString

  "EBTI EORI Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouHaveEORINumber.options.head.value))

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

    "return OK if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "redirect to next page when valid data is submitted and no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", (DoYouHaveEORINumber.options.head.value)))
      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }
  }
}
