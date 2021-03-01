package views

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.verified_user_error

class VerifiedUserErrorViewSpec extends ViewBehaviours {

  def view: () => HtmlFormat.Appendable = () =>
    new verified_user_error(mainTemplate)(frontendAppConfig)(fakeRequest, messages)

  val doc = asDocument(view())

  "Verified User Error view" must {

    behave like normalPage(view, "noAccessVerified")

    "contain heading ID" in {
      doc.getElementsByTag("h1").attr("id") mustBe "verified-unauthorised-heading"
    }

    "contain the correct content" in {
      val content = doc.select("article > p")

      content.first().text mustBe "You need to sign in with a different Government Gateway user ID."
      content.get(1).text mustBe "If you are still unable to access this service, contact HMRC."
      content.get(2).text mustBe "Telephone:"
      content.get(3).text mustBe "0300 200 3310"
      content.get(4).text mustBe "Textphone:"
      content.get(5).text mustBe "0300 200 3319"
      content.get(6).text mustBe "Outside UK:"
      content.get(7).text mustBe "+44 161 931 9070"
      content.get(8).text mustBe "Opening times:"
      content.get(9).text mustBe "Monday to Friday: 8am to 6pm"
      content.last().text mustBe "Closed weekends and bank holidays"
    }

    "contain a link that points to the correct destination" in {
      doc.getElementById("sign-in-different-user-id").attr("href") mustBe "http://localhost:9020/business-account/sso-sign-out?continueUrl=%2Fbusiness-account"
    }

  }
}
