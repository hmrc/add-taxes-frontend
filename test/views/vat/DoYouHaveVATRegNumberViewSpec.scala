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

import play.api.data.Form
import forms.vat.DoYouHaveVATRegNumberFormProvider
import models.vat.DoYouHaveVATRegNumber
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.doYouHaveVATRegNumber

class DoYouHaveVATRegNumberViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "doYouHaveVATRegNumber"

  val form = new DoYouHaveVATRegNumberFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => doYouHaveVATRegNumber(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => doYouHaveVATRegNumber(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "DoYouHaveVATRegNumber view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "do-you-have-vat-reg-number"
    }
  }

  "DoYouHaveVATRegNumber view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- DoYouHaveVATRegNumber.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      "include radio button hint text" in {
        val doc = asDocument(createViewUsingForm(form))
        assertContainsText(doc, "This is the 9-digit number on your VAT registration certificate.")
      }
    }

    for (option <- DoYouHaveVATRegNumber.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- DoYouHaveVATRegNumber.options.filterNot(_ == option)) {
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
