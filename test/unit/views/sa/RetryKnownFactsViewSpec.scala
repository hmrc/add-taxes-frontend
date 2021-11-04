package views.sa

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.retryKnownFacts

class RetryKnownFactsViewSpec extends ViewBehaviours {

  val serviceInfoContent = HtmlFormat.empty
  val ninoBoolean: Boolean = true
  val btaOrigin: String = "bta-sa"


  def createView: (String) => HtmlFormat.Appendable = (origin: String) =>
    new retryKnownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, origin, ninoBoolean)(serviceInfoContent)(fakeRequest, messages)


  "retryKnownFacts view" must {
    "contain heading ID" in {
      val doc = asDocument(createView(btaOrigin))
      doc.getElementsByTag("h1").attr("id") mustBe "retry-knownFacts-header"
    }
if(ninoBoolean) {

  "contain link to sign in with different account" in {
    val doc = asDocument(createView(btaOrigin))
    assertLinkById(
      doc,
      linkId = "sign-in-different-link",
      expectedText = "sign in with a different account",
      expectedUrl = "/business-account/add-tax/self-assessment/nino-sign-out?origin=bta-sa",
      expectedGAEvent = "link - click: Sign in with different account"
    )
  }

} else {

  "contain link to contact hmrc" in {
    val doc = asDocument(createView(btaOrigin))
    assertLinkById(
      doc,
      linkId = "online-services-helpdesk-link",
      expectedText = "contact HMRC(opens in new tab)",
      expectedUrl = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/online-services-helpdesk",
      expectedGAEvent = "link - click: Retry Knownfacts Gov Uk Online Services Help Desk",
      expectedOpensInNewTab = true
    )
  }

}

}
}
