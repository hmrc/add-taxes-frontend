package controllers.$package$

import play.api.data.Form
import play.api.libs.json.JsString
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.FakeNavigator
import controllers.actions.{FakeServiceInfoAction, _}
import controllers._
import play.api.test.Helpers._
import forms.$package$.$className$FormProvider
import identifiers.$className$Id
import models.$package$.$className$
import play.twirl.api.HtmlFormat
import views.html.$package$.$className;format="decap"$

class $className$ControllerSpec extends ControllerSpecBase {

  def onwardRoute = controllers.routes.IndexController.onPageLoad()

  val formProvider = new $className$FormProvider()
  val form = formProvider()

  def controller() = new $className$Controller(
    frontendAppConfig, messagesApi, new FakeNavigator(desiredRoute = onwardRoute), FakeAuthAction, FakeServiceInfoAction, formProvider
  )

  def viewAsString(form: Form[_] = form) = $className;format="decap"$(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "$className$ Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", $className$.options.head.value))

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

    for(option <- $className$.options) {
      s"redirect to next page when '\${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", (option.value)))
        val result = controller().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }
  }
}
