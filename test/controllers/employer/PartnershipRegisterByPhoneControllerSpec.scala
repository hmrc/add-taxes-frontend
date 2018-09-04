package controllers.employer

import controllers._
import controllers.actions._
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.employer.partnershipRegisterByPhone

class PartnershipRegisterByPhoneControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PartnershipRegisterByPhoneController(frontendAppConfig, messagesApi, FakeAuthAction, FakeServiceInfoAction)

  def viewAsString() = partnershipRegisterByPhone(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "PartnershipRegisterByPhone Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}




