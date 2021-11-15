package views.other.ppt

import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.other.ppt.you_need_a_ppt_ref

class YouNeedAPptRefViewSpec extends ViewBehaviours {

  val messagePrefix: String = "needPpt"

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new you_need_a_ppt_ref(mainTemplate)(frontendAppConfig)(serviceInfoContent)(fakeRequest, messages)

  "do_you_have_a_ppt_reference view" must {
    behave like normalPage(createView, messagePrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "you-need-ppt-ref"
    }

    "contain 'Register for the Plastic Packaging Tax (PPT)' for the link text and the right href" in {
      val doc = asDocument(createView())
      doc.getElementById("ppt-register").attr("href") mustBe "http://localhost:8505/register-for-plastic-packaging-tax/public-body-need-ppt-number"
      doc.getElementById("ppt-register").text mustBe "Register for the Plastic Packaging Tax (PPT)"
    }

    "contain 'contact HMRC (opens in new tab)' for the link text and the right href" in {
      val doc = asDocument(createView())
      doc.getElementById("contact-hmrc").attr("href") mustBe "https://www.tax.service.gov.uk/contact/report-technical-problem?newTab=true&service=GForm"
      doc.getElementById("contact-hmrc").text mustBe "contact HMRC (opens in new tab)"
    }

    "contain 'Back Home' for the button text and the right href" in {
      val doc = asDocument(createView())
      doc.getElementById("back-home-button").attr("href") mustBe "http://localhost:9020/business-account"
      doc.getElementById("back-home-button").text mustBe "Back to home"
    }
  }

}
