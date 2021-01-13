package views.sa

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.retryKnownFacts

class RetryKnownFactsViewSpec extends ViewBehaviours {

  val serviceInfoContent = HtmlFormat.empty
  val btaOrigin: String = "bta-sa"

  def createView: (String) => HtmlFormat.Appendable = (origin: String) =>
    new retryKnownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, origin)(serviceInfoContent)(fakeRequest, messages)


  "retryKnownFacts view" must {
    "contain heading ID" in {
      val doc = asDocument(createView(btaOrigin))
      doc.getElementsByTag("h1").attr("id") mustBe "retry-knownFacts-header"
    }

    "contain link to contact hmrc" in {
      val doc = asDocument(createView(btaOrigin))
      assertLinkById(
        doc,
        linkId = "online-services-helpdesk-link",
        expectedText = "contact HMRC (opens in new tab)",
        expectedUrl = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/online-services-helpdesk",
        expectedGAEvent = "link - click: Retry Knownfacts Gov Uk Online Services Help Desk",
        expectedOpensInNewTab = true
      )
    }

  }
}
