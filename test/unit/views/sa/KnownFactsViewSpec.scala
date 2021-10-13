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

import forms.sa.KnownFactsNinoFormProvider
import models.sa.{AreYouSelfEmployed, KnownFacts, KnownFactsNino}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.KnownFactsFormValidator
import views.behaviours.ViewBehaviours
import views.html.sa.knownFacts

class KnownFactsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "knownFacts"
  val mockKnownFactsValidator: KnownFactsFormValidator = injector.instanceOf[KnownFactsFormValidator]
  val btaOrigin: String = "bta-sa"

  val formProvider = new KnownFactsNinoFormProvider(mockKnownFactsValidator, frontendAppConfig)
  val form: Form[KnownFactsNino] = formProvider()
  val serviceInfoContent = HtmlFormat.empty


  def createView: (String) => HtmlFormat.Appendable = (origin: String) =>
    new knownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, form, origin, false)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: (Form[KnownFactsNino], String) => HtmlFormat.Appendable = (form: Form[KnownFactsNino], origin: String) =>
    new knownFacts(formWithCSRF, mainTemplate)(frontendAppConfig, form, origin, false)(serviceInfoContent)(fakeRequest, messages)

  "knownFacts view" must {
    "contain heading ID" in {
      val doc = asDocument(createView(btaOrigin))
      doc.getElementsByTag("h1").attr("id") mustBe "known-facts-nino-heading"
    }
  }

  "knownFacts view" when {
    "rendered" must {
      "contain input for nino value" in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("nino" -> "aa000000a")), btaOrigin))
        assertInputValueById(doc, "nino", "nino", "aa000000a")
      }
      "contain link to postcode page" in {
        val doc = asDocument(createViewUsingForm(form, btaOrigin))
        assertLinkById(
          doc,
          linkId = "postcode-button",
          expectedText = "I do not know my National Insurance number.",
          expectedUrl = "/business-account/add-tax/self-assessment/postcode?origin=bta-sa",
          expectedGAEvent = "GaEvent:Click:PostcodeLink"
        )
      }
    }
      "invalid data is sent" must {
        "prepend title with Error: " in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("nino" -> "")), btaOrigin))
          val title = messages("site.service_title", messages(s"enterKnownFacts.nino.title"))

          assertEqualsMessage(doc, "title", "error.browser.title", title)
        }
        "show error message when nino is in the wrong format" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("nino" -> "aaaaaaaaa")), btaOrigin))

          errorMessageValue(doc) mustBe "Enter a National Insurance number in the right format, for example QQ 12 34 56 C"
        }
        "show error message when nino is the wrong length" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("nino" -> "aa0000000a")), btaOrigin))

          errorMessageValue(doc) mustBe "Enter a National Insurance number in the right format, for example QQ 12 34 56 C"
        }
        "show error message when input is blank" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("nino" -> "")), btaOrigin))

          errorMessageValue(doc) mustBe "Enter your National Insurance number"
        }
      }
    }
}
