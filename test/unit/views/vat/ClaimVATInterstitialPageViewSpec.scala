package views.vat

import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.vat.claimVATInterstitialPage

class ClaimVATInterstitialPageViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "claimVATInterstitialPage"

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new claimVATInterstitialPage(formWithCSRF, mainTemplate)(frontendAppConfig)(serviceInfoContent)(fakeRequest, messages)

  "ClaimVATInterstitialPage view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "claim-vat-interstitial-heading"
    }

    "include correct paragraph and bullet points" in {
      val doc = asDocument(createView())
      assertContainsText(
        doc,
        "Notification of VAT Registration letter. You will have received this by post when you first registered for VAT"
      )
      assertContainsText(
        doc,
        "If you are currently submitting VAT returns you will also need to tell us:"
      )
      assertContainsText(
        doc,
        "5. The month of your latest VAT accounting period"
      )
      assertContainsText(
        doc,
        "It is 9 numbers, sometimes with ‘GB’ at the start. For example, 123456789 or GB123456789."
      )
    }
  }

  "ClaimVATInterstitialPage view" when {
    "rendered" must {
      "have all five headings and have the link that opens in new tab" in {
        val doc = asDocument(createView())
        val headingSection = doc.getElementsByClass("govuk-heading-s")

        headingSection.size() mustBe 5

        assertLinkById(doc, "check-vat-number-link", expectedText = "Check a UK VAT Number service (opens in new tab)",
          "https://www.gov.uk/check-uk-vat-number",expectedOpensInNewTab = true)

        assertRenderedById(doc, "continue")
      }
    }
  }
}
