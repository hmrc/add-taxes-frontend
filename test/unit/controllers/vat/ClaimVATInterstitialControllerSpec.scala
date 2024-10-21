package controllers.vat


import controllers.ControllerSpecBase
import controllers.actions.DataRetrievalAction
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.vat.claimVATInterstitialPage

class ClaimVATInterstitialControllerSpec extends ControllerSpecBase {

  val view: claimVATInterstitialPage = injector.instanceOf[claimVATInterstitialPage]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): ClaimVATInterstitialController = {
    new ClaimVATInterstitialController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      view
    )
  }

  "Claim VAT Interstitial Page Controller" must {
    "return 200 and the correct view for a GET" in {
      val expected = new claimVATInterstitialPage(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString
      val result = controller().onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe expected
    }
  }
}