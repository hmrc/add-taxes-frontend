package views.sa

import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.sa.requestedAccess

class RequestedAccessViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "requestedAccess"

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new requestedAccess(mainTemplate)(frontendAppConfig)(serviceInfoContent)(fakeRequest, messages)

  val doc = asDocument(createView())

  "requestedAccess view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading with correct ID and content" in {
      val heading = doc.select("h1")
      heading.attr("id") mustBe "requested-access-heading"
      heading.text() mustBe "You've requested access to Self Assessment"
    }

    "have the correct info content" in {
      val paragraphs = doc.select(".govuk-body")
      paragraphs.first().text() mustBe "You'll receive an activation code in the post within 10 days."
      paragraphs.get(1).text() mustBe "You need to use the activation code to get access to the tax or scheme."
    }

    "have a continue link, styled like a button, going to the correct destination" in {
      val button = doc.select("#continue")
      button.text() mustBe "Continue"
      button.attr("class") mustBe "button"
      button.attr("href") mustBe "http://localhost:9020/business-account"
    }
  }
}