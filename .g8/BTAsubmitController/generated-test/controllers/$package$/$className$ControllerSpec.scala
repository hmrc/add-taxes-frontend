package controllers.$package$

import controllers._
import controllers.actions._
import forms.$modelPackage$.$model$FormProvider
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.FakeNavigator

class $className$ControllerSpec extends ControllerSpecBase {

  def onwardRoute = controllers.routes.IndexController.onPageLoad()

  val formProvider = new $model$FormProvider()
  val form = formProvider()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new $className$Controller(frontendAppConfig, messagesApi, FakeAuthAction,
      new FakeNavigator[Call](desiredRoute = onwardRoute), FakeServiceInfoAction, formProvider)

  def viewAsString(form: Form[_] = form) = ???

  "$className$ Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> "ValidValue")
      val result = controller().onSubmit(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return BadRequest when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> "")
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller().onSubmit(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }
  }
}




