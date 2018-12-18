package views.sa

import play.api.data.Form
import forms.sa.YourSaIsNotInThisAccountFormProvider
import models.sa.YourSaIsNotInThisAccount
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.yourSaIsNotInThisAccount

class YourSaIsNotInThisAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "yourSaIsNotInThisAccount"

  val form = new YourSaIsNotInThisAccountFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => yourSaIsNotInThisAccount(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => yourSaIsNotInThisAccount(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "YourSaIsNotInThisAccount view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "YourSaIsNotInThisAccount view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- YourSaIsNotInThisAccount.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- YourSaIsNotInThisAccount.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- YourSaIsNotInThisAccount.options.filterNot(_ == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))
        val title = messages("site.service_title", messages(s"$messageKeyPrefix.title"))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }
    }
  }
}
