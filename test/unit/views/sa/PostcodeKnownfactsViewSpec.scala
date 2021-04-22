package views.sa

import forms.sa.KnownFactsPostcodeFormProvider
import models.sa.{KnownFacts, KnownFactsPostcode}
import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import utils.KnownFactsFormValidator
import views.behaviours.ViewBehaviours
import views.html.sa.postcodeKnownFacts

class PostcodeKnownFactsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "knownFacts"
  val mockKnownFactsValidator: KnownFactsFormValidator = injector.instanceOf[KnownFactsFormValidator]
  val formProvider = new KnownFactsPostcodeFormProvider(mockKnownFactsValidator, frontendAppConfig)
  val form = formProvider()
  val serviceInfoContent = HtmlFormat.empty
  val btaOrigin: String = "bta-sa"

  def createView: (String) => HtmlFormat.Appendable = (origin: String) =>
    new postcodeKnownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, form, origin)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: (Form[KnownFactsPostcode], String) => HtmlFormat.Appendable = (form: Form[KnownFactsPostcode], origin: String) =>
    new postcodeKnownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, form, origin)(serviceInfoContent)(fakeRequest, messages)

  "postcodeKnownfacts view" must {
    "contain heading ID" in {
      val doc = asDocument(createView(btaOrigin))
      doc.getElementsByTag("h1").attr("id") mustBe "known-facts-heading"
    }
  }

  "postcodeKnownfacts view" when {
    "rendered" must {
      "contain input for postcode value" in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("postcode" -> "zz11zz")), btaOrigin))
        assertInputValueById(doc, "postcode", "postcode", "zz11zz")
      }
      "contain checkbox for is abroad" in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("isAbroad" -> "Y")), btaOrigin))
        assertInputValueById(doc, "isAbroad", "isAbroad", "Y")
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val form = formProvider().withError(FormError("postcode", "enterKnownFacts.postcode.error.required")).withError(FormError("isAbroad", "enterKnownFacts.postcode.error.required"))
        val doc = asDocument(createViewUsingForm(form.bind(Map("postcode" -> "")), btaOrigin))
        val title = messages("site.service_title", messages(s"enterKnownFacts.postcode.title"))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }
      "show error message when postcode is in the wrong format" in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("postcode" -> "sssssss")), btaOrigin))

        errorMessageValue(doc) mustBe "Enter a postcode in the right format"
      }
      "show error message when postcode and isAbroad is checked" in {
        val form = formProvider().withError(FormError("postcode", "enterKnownFacts.postcode.error.required")).withError(FormError("isAbroad", "enterKnownFacts.postcode.error.required"))

        val doc = asDocument(createViewUsingForm(form.bind(Map("postcode" -> "zz11zz", "isAbroad" -> "Y")), btaOrigin))

        errorMessageValue(doc) mustBe "Enter a postcode or select I live abroad Enter a postcode or select I live abroad"
      }
      "show error message when input is blank" in {
        val form = formProvider().withError(FormError("postcode", "enterKnownFacts.postcode.error.required")).withError(FormError("isAbroad", "enterKnownFacts.postcode.error.required"))

        val doc = asDocument(createViewUsingForm(form.bind(Map("postcode" -> "")), btaOrigin))

        errorMessageValue(doc) mustBe "Enter a postcode or select I live abroad Enter a postcode or select I live abroad"
      }
    }
  }

}