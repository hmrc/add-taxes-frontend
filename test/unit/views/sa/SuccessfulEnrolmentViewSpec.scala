package views.sa

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.successfulEnrolment

class SuccessfulEnrolmentViewSpec extends ViewBehaviours {

  val serviceInfoContent = HtmlFormat.empty
  val btaOrigin: String = "bta-sa"
  val ptaOrigin: String = "pta-sa"

  def createView: (String) => HtmlFormat.Appendable = (origin: String) =>
    new successfulEnrolment(mainTemplate)(frontendAppConfig, origin)(serviceInfoContent)(fakeRequest, messages)

  "successfulEnrolment view" when {
    "given an origin of bta-sa" must {
      val doc = asDocument(createView(btaOrigin))
      "contain a heading" which {

        val heading = doc.getElementsByTag("h1")
        "has the correct id" in {
          heading.attr("id") mustBe "sa-successful-enrolment"
        }
        "has the correct text" in {
          heading.text() mustBe "You now have access to Self Assessment"
        }
      }
      "have a continue link" which {
        val continue = doc.getElementById("continue")
        "is styled like a button" in {
          continue.className() mustBe "govuk-button"
        }
        "has the correct text" in {
          continue.text mustBe "Continue"
        }
        "goes to the correct location" in {
          continue.attr("href") mustBe "http://localhost:9020/business-account"
        }
      }
    }
  }
  "given an origin of pta-sa" must {
    val doc = asDocument(createView(ptaOrigin))
    "have a continue link" which {
      val continue = doc.getElementById("continue")
      "is styled like a button" in {
        continue.className() mustBe "govuk-button"
      }
      "has the correct text" in {
        continue.text mustBe "Continue"
      }
      "goes to the correct location" in {
        continue.attr("href") mustBe "http://localhost:9232/personal-account"
      }
    }
  }
}
