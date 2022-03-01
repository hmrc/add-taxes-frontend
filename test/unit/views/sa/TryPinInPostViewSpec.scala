package views.sa

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.tryPinInPost

class TryPinInPostViewSpec extends ViewBehaviours {
  val serviceInfoContent = HtmlFormat.empty
  val btaOrigin: String = "bta-sa"

  def createView: (String, Option[String]) => HtmlFormat.Appendable = (origin: String, status: Option[String]) =>
    new tryPinInPost(formWithCSRF, mainTemplate)(frontendAppConfig, status, origin)(serviceInfoContent)(fakeRequest, messages)

  "tryPinInPost view" must {
    "contain heading ID" in {
      val doc = asDocument(createView(btaOrigin, Some("")))
      doc.getElementsByTag("h1").attr("id") mustBe "tryPinInPost"
    }

    "contains failed content when Failed status is passed to the view" in {
      val doc = asDocument(createView(btaOrigin, Some("Failed")))
      assertLinkById(
        doc,
        linkId = "iv-route-link",
        expectedText = "confirm your identity online",
        expectedUrl = "/business-account/add-tax/self-assessment/try-iv?origin=bta-sa"
      )
    }

    "contains LockedOut content when LockedOut status is passed to the view" in {
      val doc = asDocument(createView(btaOrigin, Some("LockedOut")))
      doc.getElementsByAttribute("href") mustNot contain("confirm your identity online")
      doc.getElementsByTag("p").text() must include("You can request an activation code in the post. This will take up to 10 days.")
    }

    "contains MatchingError content when MatchingError status is passed to the view" in {
      val doc = asDocument(createView(btaOrigin, Some("MatchingError")))
      doc.getElementsByAttribute("href") mustNot contain("confirm your identity online")
      doc.getElementsByTag("p").text() must include("You can request an activation code in the post. This will take up to 10 days.")
    }
  }
}
