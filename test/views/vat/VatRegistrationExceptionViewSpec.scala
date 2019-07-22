/*
 * Copyright 2019 HM Revenue & Customs
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
import forms.vat.VatRegistrationExceptionFormProvider
import models.vat.VatRegistrationException
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.vatRegistrationException

class VatRegistrationExceptionViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "vatRegistrationException"

  val form = new VatRegistrationExceptionFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => vatRegistrationException(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => vatRegistrationException(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def viewIncludes(s: String): Unit = asDocument(createView()).text() must include(s)

  "VatRegistrationException view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "include radio button hint text" in {
      viewIncludes(
        " You can apply if your VAT taxable turnover has temporarily gone over the threshold and you do not think it will go over the deregistration threshold of £83,000 in the next 12 months. ")
    }
  }

  "VatRegistrationException view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- VatRegistrationException.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- VatRegistrationException.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- VatRegistrationException.options.filterNot(_ == option)) {
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
