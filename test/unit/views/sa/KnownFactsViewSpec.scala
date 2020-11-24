/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views.sa

import forms.sa.KnownFactsFormProvider
import models.sa.{AreYouSelfEmployed, KnownFacts}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.KnownFactsFormValidator
import views.behaviours.ViewBehaviours
import views.html.sa.knownFacts

class KnownFactsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "knownFacts"
  val mockKnownFactsValidator: KnownFactsFormValidator = injector.instanceOf[KnownFactsFormValidator]


  val formProvider = new KnownFactsFormProvider(mockKnownFactsValidator, frontendAppConfig)
  val form: Form[KnownFacts] = formProvider()
  val serviceInfoContent = HtmlFormat.empty


  def createView: () => HtmlFormat.Appendable = () =>
    new knownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[KnownFacts] => HtmlFormat.Appendable = (form: Form[KnownFacts]) =>
    new knownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "knownFacts view" must {
    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "known-facts-heading"
    }
  }

  "knownFacts view" when {
    "rendered" must {
      "contain input for nino value" in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("nino" -> "aa000000a"))))
        assertInputValueById(doc, "nino", "nino", "aa000000a")
      }
      "contain link to postcode page" in {
        val doc = asDocument(createViewUsingForm(form))
        assertLinkById(
          doc,
          linkId = "postcode-button",
          expectedText = "I do not know my National Insurance number.",
          expectedUrl = "/business-account/add-tax/self-assessment/postcode",
          expectedGAEvent = "GaEvent:Click:PostcodeLink"
        )
      }
    }
      "invalid data is sent" must {
        "prepend title with Error: " in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("nino" -> ""))))
          val title = messages("site.service_title", messages(s"enterKnownFacts.nino.title"))

          assertEqualsMessage(doc, "title", "error.browser.title", title)
        }
        "show error message when nino is in the wrong format" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("nino" -> "aaaaaaaaa"))))

          errorMessageValue(doc) mustBe "Enter a National Insurance number in the right format, for example QQ 12 34 56 C"
        }
        "show error message when nino is the wrong length" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("nino" -> "aa0000000a"))))

          errorMessageValue(doc) mustBe "Enter a National Insurance number in the right format, for example QQ 12 34 56 C"
        }
        "show error message when input is blank" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("nino" -> ""))))

          errorMessageValue(doc) mustBe "Enter your National Insurance number"
        }
      }
    }
}
