package controllers.other.ppt

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.PptSwitch
import config.featureToggles.FeatureToggleSupport.isEnabled
import controllers.ControllerSpecBase
import handlers.ErrorHandler
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.i18n.MessagesApi
import play.api.mvc.Call
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import play.twirl.api.HtmlFormat
import views.html.other.ppt.you_need_a_ppt_ref

class YouNeedAPptRefControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad
  val serverErrorTemplate = "An error has occurred"

  implicit val messagesAPI: MessagesApi = mcc.messagesApi
  implicit val config: FrontendAppConfig = frontendAppConfig

  val view: you_need_a_ppt_ref = injector.instanceOf[you_need_a_ppt_ref]

  val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  def controller(pptSwitch: Boolean = true) =
    new YouNeedAPptRefController(
      mcc,
      errorHandler,
      FakeAuthAction,
      FakeServiceInfoAction,
      view,
      frontendAppConfig
    ) {
      override val pptFeatureSwitch: Boolean = pptSwitch
    }

  def viewAsString(): String =
    new you_need_a_ppt_ref(mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  if (isEnabled(PptSwitch)) {
  "YouNeedAPptRef Controller" must {
      "return OK and the correct view for a GET" in {
        val result = controller().onPageLoad()(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString()
      }

    "return INTERNAL_SERVER_ERROR when the flag is set to false" in {
      val result = controller(pptSwitch = false).onPageLoad()(fakeRequest)

      status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

}
