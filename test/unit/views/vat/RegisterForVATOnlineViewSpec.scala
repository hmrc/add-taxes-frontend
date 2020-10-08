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

import forms.vat.RegisterForVATOnlineFormProvider
import models.vat.RegisterForVATOnline
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.vat.registerForVATOnline

class RegisterForVATOnlineViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerForVATOnline"

  val form = new RegisterForVATOnlineFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new registerForVATOnline(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) =>
    new registerForVATOnline(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "RegisterForVATOnline view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "RegisterForVATOnline view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- RegisterForVATOnline.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
        }
      }

      "include header" in {
        val doc = asDocument(createViewUsingForm(form))
        assertContainsText(doc, "Do you want to register for VAT online?")
      }

      "include list items" in {
        val doc = asDocument(createViewUsingForm(form))
        assertContainsText(doc, "Do you want to register for VAT online?")
        assertContainsText(doc, "need to apply for a ‘registration exception’")
        assertContainsText(doc, "are joining the Agricultural Flat Rate Scheme")
        assertContainsText(
          doc,
          "are registering the divisions or business units of a body corporate under separate VAT numbers")
        assertContainsText(doc, "have an EU business ‘distance selling’ to the UK")
        assertContainsText(doc, "have imported (‘acquired’) goods worth more than £85,000 from another EU company")
        assertContainsText(doc, "are disposing of assets on which 8th or 13th Directive refunds have been claimed")
      }
    }

    for (option <- RegisterForVATOnline.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- RegisterForVATOnline.options.filterNot(_ == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
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
