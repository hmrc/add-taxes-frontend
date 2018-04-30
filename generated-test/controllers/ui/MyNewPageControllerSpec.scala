package controllers.ui

import controllers._
import controllers.actions._
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.ui.myNewPage

class MyNewPageControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new MyNewPageController(frontendAppConfig, messagesApi, FakeAuthAction, FakeServiceInfoAction)

  def viewAsString() = myNewPage(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "MyNewPage Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}




