package controllers

import play.api.test.Helpers._
import views.html.verified_user_error

class VerifiedUserControllerSpec extends ControllerSpecBase {

  val view: verified_user_error = injector.instanceOf[verified_user_error]

  "VerifiedUser Controller" must {
    "return 200 for a GET" in {
      val result = new VerifiedUserController(frontendAppConfig, mcc, view).onPageLoad()(fakeRequest)
      status(result) mustBe OK
    }

    "return the correct view for a GET" in {
      val result = new VerifiedUserController(frontendAppConfig, mcc, view).onPageLoad()(fakeRequest)
      contentAsString(result) must include("You do not have access to this service")
    }
  }

}
