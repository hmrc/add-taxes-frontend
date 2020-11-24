package views.sa

import forms.sa.KnownFactsFormProvider
import models.sa.KnownFacts
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.KnownFactsFormValidator
import views.behaviours.ViewBehaviours
import views.html.sa.postcodeKnownFacts

class PostcodeKnownFactsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "knownFacts"
  val mockKnownFactsValidator: KnownFactsFormValidator = injector.instanceOf[KnownFactsFormValidator]
  val formProvider = new KnownFactsFormProvider(mockKnownFactsValidator, frontendAppConfig)
  val form = formProvider(true)
  val serviceInfoContent = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new postcodeKnownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[KnownFacts] => HtmlFormat.Appendable = (form: Form[KnownFacts]) =>
    new postcodeKnownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "postcodeKnownfacts view" must {
    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "known-facts-heading"
    }
  }

  "postcodeKnownfacts view" when {
    "rendered" must {
      "contain input for postcode value" in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("postcode" -> "zz11zz"))))
        assertInputValueById(doc, "postcode", "postcode", "zz11zz")
      }
      "contain checkbox for is abroad" in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("isAbroad" -> "Y"))))
        assertInputValueById(doc, "isAbroad", "isAbroad", "Y")
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("postcode" -> ""))))
        val title = messages("site.service_title", messages(s"enterKnownFacts.postcode.title"))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }
      "show error message when postcode is in the wrong format" in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("postcode" -> "sssssss"))))

        errorMessageValue(doc) mustBe "Enter a postcode in the right format"
      }
      "show error message when postcode and isAbroad is checked" in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("postcode" -> "zz11zz", "isAbroad" -> "Y"))))

        errorMessageValue(doc) mustBe "Enter a postcode or select if you live abroad"
      }
      "show error message when input is blank" in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("postcode" -> ""))))

        errorMessageValue(doc) mustBe "Enter a postcode in the right format"
      }
    }
  }

}