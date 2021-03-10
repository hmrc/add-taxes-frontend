package controllers.sa

import controllers.Assets.{OK, SEE_OTHER}
import controllers.ControllerSpecBase
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import play.twirl.api.HtmlFormat
import views.html.sa.groupIdError

class GroupIdFoundControllerSpec extends ControllerSpecBase with MockitoSugar {
  val view: groupIdError = injector.instanceOf[groupIdError]
  val btaOrigin: String = "bta-sa"

  def controller(): GroupIdFoundController = {
    new GroupIdFoundController (
      FakeAuthAction,
      FakeServiceInfoAction,
      frontendAppConfig,
      mcc,
      view
    )
  }

  def viewAsString(): String =
    new groupIdError(mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "EnrolmentSuccess Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }

}