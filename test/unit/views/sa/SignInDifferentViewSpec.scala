package views.sa

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.signInDifferent

class SignInDifferentViewSpec extends ViewBehaviours {

  val serviceInfoContent = HtmlFormat.empty
  val btaOrigin: String = "bta-sa"


  def createView: (String) => HtmlFormat.Appendable = (origin: String) =>
    new signInDifferent(mainTemplate)(frontendAppConfig, origin)(serviceInfoContent)(fakeRequest, messages)


  "retryKnownFacts view" must {
    "contain heading ID" in {
      val doc = asDocument(createView(btaOrigin))
      doc.getElementsByTag("h1").attr("id") mustBe "sign-in-different-header"
    }


  "contain link to sign in with different account" in {
    val doc = asDocument(createView(btaOrigin))
    assertLinkById(
      doc,
      linkId = "cancel-button",
      expectedText = "Cancel",
      expectedUrl = "/business-account/add-tax/self-assessment/retry-known-facts?origin=bta-sa"
    )
  }


  "contain link to contact hmrc" in {
    val doc = asDocument(createView(btaOrigin))
    assertLinkById(
      doc,
      linkId = "signout-button",
      expectedText = "Sign out",
      expectedUrl = "http://localhost:9020/business-account/sso-sign-out?continueUrl=http://localhost:9553/business-account"
    )
  }

}

}
