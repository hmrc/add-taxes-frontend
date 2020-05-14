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

package views.vat

import forms.vat.WhatIsYourVATRegNumberFormProvider
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.vat.whatIsYourVATRegNumber

class WhatIsYourVATRegNumberViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whatIsYourVATRegNumber"

  val form = new WhatIsYourVATRegNumberFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new whatIsYourVATRegNumber(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable = (form: Form[String]) =>
    new whatIsYourVATRegNumber(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def viewIncludes(s: String): Unit = asDocument(createView()).text() must include(s)

  "WhatIsYourVATRegNumber view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "include hint text - This is the 9-digit number on your VAT registration certificate. For example, 123456789." in {
      viewIncludes("This is the 9-digit number on your VAT registration certificate. For example, 123456789.")
    }

    "contain heading id" in {
      val doc = asDocument(createViewUsingForm(form))
      doc.getElementsByTag("h1").attr("id") mustBe "enter-vat-registration-heading"
    }
  }

  "WhatIsYourVATRegNumber view" when {
    "rendered" must {
      "contain a text box for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        assertRenderedByCssSelector(doc, ".form-control")
      }
    }

    "rendered" must {
      "include label for textField - What is your VAT registration number?" in {
        val doc = asDocument(createViewUsingForm(form))
        assertContainsLabel(doc, "value", "What is your VAT number?")
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))
        val title = messages("site.service_title", messages(s"$messageKeyPrefix.title"))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }

      "show error summary when there is error " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))

        assertRenderedById(doc, "error-summary-heading")
      }
    }
  }
}
