package views.other.ppt

import forms.other.ppt.PptReferenceFormProvider
import models.other.ppt.DoYouHaveAPptReference
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.other.ppt.do_you_have_a_ppt_reference

class DoYouHaveAPptRefViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "doYouHaveAPptRef"

  val form = new PptReferenceFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new do_you_have_a_ppt_reference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) =>
    new do_you_have_a_ppt_reference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "do_you_have_a_ppt_reference view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "do-you-have-a-ppt-ref-heading"
      }

    "contain radio buttons for the value" in {
       val doc = asDocument(createViewUsingForm(form))
       for (option <- DoYouHaveAPptReference.options) {
        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
         }
      }
    }

}
