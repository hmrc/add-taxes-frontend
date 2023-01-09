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

package controllers.deenrolment

import controllers.ControllerSpecBase
import forms.deenrolment.DoYouNeedToLeaveVATMOSSFormProvider
import handlers.ErrorHandler
import models.deenrolment.DoYouNeedToLeaveVATMOSS
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.FakeNavigator
import views.html.deenrolment.doYouNeedToLeaveVATMOSS

class DoYouNeedToLeaveVATMOSSControllerSpec extends ControllerSpecBase {

  implicit val messageAPI: MessagesApi = mcc.messagesApi
  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val formProvider = new DoYouNeedToLeaveVATMOSSFormProvider()
  val form: Form[DoYouNeedToLeaveVATMOSS] = formProvider()

  val view: doYouNeedToLeaveVATMOSS = injector.instanceOf[doYouNeedToLeaveVATMOSS]

  val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  def controller(desiredRoute: Either[String, Call] = Right(onwardRoute)): DoYouNeedToLeaveVATMOSSController = {
    new DoYouNeedToLeaveVATMOSSController(
      frontendAppConfig,
      mcc,
      new FakeNavigator[Either[String, Call]](desiredRoute = desiredRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      errorHandler,
      view
    )
  }

  def viewAsString(form: Form[_] = form): String =
    new doYouNeedToLeaveVATMOSS(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "DoYouNeedToLeaveVATMOSS Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouNeedToLeaveVATMOSS.options.head.value)).withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return OK if no existing data is found" in {
      val result = controller().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
    }

    for (option <- DoYouNeedToLeaveVATMOSS.options) {
      s"redirect to next page when '${option.value}' is submitted and no existing data is found" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value)).withMethod("POST")
        val result = controller().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "return internal server error" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouNeedToLeaveVATMOSS.Yes.toString)).withMethod("POST")
      val result = controller(desiredRoute = Left("")).onSubmit()(postRequest)

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentAsString(result) must include("Sorry, we’re experiencing technical difficulties")
    }
  }
}
