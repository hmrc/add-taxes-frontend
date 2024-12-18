package views.sa

import forms.sa.CaptureSAUTRFormProvider
import models.sa.DoYouHaveSAUTR
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.sa.captureSAUTR

class CaptureSAUTRViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "captureSAUTR"

  val form = new CaptureSAUTRFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new captureSAUTR(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) =>
    new captureSAUTR(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "CaptureSAUTR view" must {

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "do-you-have-sa-utr"
    }
  }

  "DoYouHaveSAUTR view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- DoYouHaveSAUTR.options) {
          assertContainsRadioButton(doc, "value", "value", "Yes", isChecked = false)
        }
      }
    }

      s"rendered with a value of Yes" must {
        s"have the Yes radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> "Yes"))))
          assertContainsRadioButton(doc, "value", "value", "Yes", isChecked = true)
        }
      }


    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))
        val title = messages("site.service_title", messages(s"$messageKeyPrefix.title"))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }
    }

    "when clicking Yes rendering sautr input box" must {
      "sautr should have value: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> "Yes", "sautrValue"->"1234567890"))))
        val inputBox = doc.getElementById("sautrValue")
        assert(inputBox.attr("name") == "sautrValue", s"\n\n input box does not rendered")
        assert(inputBox.`val`() == "1234567890", s"\n\n sautr value is not correct")

      }
    }
  }
}
