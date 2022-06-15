package controllers.other.ppt

import config.FrontendAppConfig
import controllers.ControllerSpecBase
import forms.other.ppt.PptReferenceFormProvider
import handlers.ErrorHandler
import models.other.ppt.DoYouHaveAPptReference
import play.api.data.Form
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, SEE_OTHER}
import play.api.i18n.MessagesApi
import play.api.mvc.Call
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import play.twirl.api.HtmlFormat
import utils.FakeNavigator
import views.html.other.ppt.do_you_have_a_ppt_reference

class DoYouHaveAPPTRefControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val serverErrorTemplate = "An error has occurred"

  implicit val messagesAPI: MessagesApi = mcc.messagesApi
  implicit val config: FrontendAppConfig = frontendAppConfig

  val view: do_you_have_a_ppt_reference = injector.instanceOf[do_you_have_a_ppt_reference]

  val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  val formProvider = new PptReferenceFormProvider()
  val form: Form[DoYouHaveAPptReference] = formProvider()

  def controller(desiredRoute: Call = onwardRoute, pptSwitch: Boolean = true) =
    new DoYouHaveAPPTRefController(
      mcc,
      new FakeNavigator[Call](desiredRoute = desiredRoute),
      errorHandler,
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      view,
      frontendAppConfig
    ) {
      override val pptFeatureSwitch: Boolean = pptSwitch
    }

  def viewAsString(form: Form[_] = form): String =
    new do_you_have_a_ppt_reference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

    "DoYouHaveAPPTRef Controller" must {
      "return OK and the correct view for a GET" in {
        val result = controller().onPageLoad()(fakeRequest.withMethod("GET"))

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString()
      }

      "redirect to the next page when valid data is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouHaveAPptReference.options.head.value)).withMethod("POST")

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

      "return INTERNAL_SERVER_ERROR when switch is turned off" in {
        val result = controller(pptSwitch = false).onPageLoad()(fakeRequest.withMethod("GET"))
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

}
