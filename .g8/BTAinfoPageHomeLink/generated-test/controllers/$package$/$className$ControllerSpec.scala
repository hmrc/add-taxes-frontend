package controllers.$package$

import controllers._
import controllers.actions._
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.$package$.$className;format="decap"$

class $className$ControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new $className$Controller(frontendAppConfig, messagesApi, FakeAuthAction, FakeServiceInfoAction)

  def viewAsString() = $className;format="decap"$(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "$className$ Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
