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

package controllers

import connectors.FakeDataCacheConnector
import controllers.actions.{FakeServiceInfoAction, _}
import forms.OtherTaxesFormProvider
import models.OtherTaxes
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import play.api.data.Form
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import utils.{FakeNavigator, RadioOption}
import views.html.otherTaxes

class OtherTaxesControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.IndexController.onPageLoad()

  def requestWithEnrolments(keys: String*): ServiceInfoRequest[AnyContent] = {
    val enrolments = Enrolments(keys.map(Enrolment(_)).toSet)
    ServiceInfoRequest[AnyContent](AuthenticatedRequest(FakeRequest(), "", enrolments), HtmlFormat.empty)
  }

  val formProvider = new OtherTaxesFormProvider()
  val form = formProvider()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new OtherTaxesController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute), FakeAuthAction,
      FakeServiceInfoAction, formProvider)

  def viewAsString(form: Form[_] = form) = otherTaxes(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "OtherTaxes Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", OtherTaxes.options.head.value))

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
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", OtherTaxes.options.head.value))
      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }
  }

  "display None if the user has ATWD and AWRS " in {
    val request = requestWithEnrolments("HMCE-ATWD-ORG", "HMRC-AWRS-ORG")
    val result = controller(dontGetAnyData).getOptions(request)

    result.length mustBe 0
  }
  "display all options" in {
    val request = requestWithEnrolments()
    val result = controller(dontGetAnyData).getOptions(request)

    result.length mustBe 1
    result.head mustBe RadioOption("otherTaxes", "alcoholAndTobaccoWholesalingAndWarehousing")
  }
}
