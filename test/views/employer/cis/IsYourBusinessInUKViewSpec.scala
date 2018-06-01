package views.employer.cis

import play.api.data.Form
import forms.employer.cis.IsYourBusinessInUKFormProvider
import models.employer.cis.IsYourBusinessInUK
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.cis.isYourBusinessInUK

class IsYourBusinessInUKViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "isYourBusinessInUK"

  val form = new IsYourBusinessInUKFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => isYourBusinessInUK(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => isYourBusinessInUK(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "IsYourBusinessInUK view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "IsYourBusinessInUK view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- IsYourBusinessInUK.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- IsYourBusinessInUK.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- IsYourBusinessInUK.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))

        assertEqualsMessage(doc, "title", "error.browser.title", messages(s"$messageKeyPrefix.title"))
      }
    }
  }
}
