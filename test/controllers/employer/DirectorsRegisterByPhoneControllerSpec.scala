package controllers.employer

import controllers._
import controllers.actions._
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.employer.directorsRegisterByPhone

class DirectorsRegisterByPhoneControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new DirectorsRegisterByPhoneController(frontendAppConfig, messagesApi, FakeAuthAction, FakeServiceInfoAction)

  def viewAsString() = directorsRegisterByPhone(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "DirectorsRegisterByPhone Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}




