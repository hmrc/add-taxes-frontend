package views.other.oil.reverb

import play.api.data.Form
import forms.other.oil.reverb.MyNewPageFormProvider
import models.other.oil.reverb.MyNewPage
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.oil.reverb.myNewPage

class MyNewPageViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "myNewPage"

  val form = new MyNewPageFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => myNewPage(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => myNewPage(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "MyNewPage view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "MyNewPage view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- MyNewPage.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- MyNewPage.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- MyNewPage.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
