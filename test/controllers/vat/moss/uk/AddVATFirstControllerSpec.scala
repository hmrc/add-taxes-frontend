package controllers.vat.moss.uk

import controllers._
import controllers.actions._
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.vat.moss.uk.addVATFirst

class AddVATFirstControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new AddVATFirstController(frontendAppConfig, messagesApi, FakeAuthAction, FakeServiceInfoAction)

  def viewAsString() = addVATFirst(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "AddVATFirst Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
