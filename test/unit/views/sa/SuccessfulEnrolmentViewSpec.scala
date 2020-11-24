package views.sa

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.successfulEnrolment

class SuccessfulEnrolmentViewSpec extends ViewBehaviours {

  val serviceInfoContent = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new successfulEnrolment(mainTemplate)(frontendAppConfig)(serviceInfoContent)(fakeRequest, messages)

  "successfulEnrolment view" must {
    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "sa-successful-enrolment"
    }
  }
}
