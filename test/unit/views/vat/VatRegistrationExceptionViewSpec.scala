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

import forms.vat.VatRegistrationExceptionFormProvider
import models.vat.VatRegistrationException
import play.api.data.Form
import play.api.mvc.Request
import play.twirl.api.{Html, HtmlFormat}
import service.ThresholdService
import views.behaviours.ViewBehaviours
import views.html.vat.vatRegistrationException

class VatRegistrationExceptionViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "vatRegistrationException"

  val form = new VatRegistrationExceptionFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  val thresholdService: ThresholdService = injector.instanceOf[ThresholdService]
  implicit val request: Request[_] = fakeRequest

  def createView: () => HtmlFormat.Appendable = () =>
    new vatRegistrationException(formWithCSRF, mainTemplate)(
      frontendAppConfig, form, thresholdService.formattedVatDeregThreshold())(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) =>
    new vatRegistrationException(formWithCSRF, mainTemplate)(
      frontendAppConfig, form, thresholdService.formattedVatDeregThreshold())(serviceInfoContent)(fakeRequest, messages)

  "VatRegistrationException view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "include radio button hint text" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(
        doc,
        "You can apply if your VAT taxable turnover has temporarily gone over the threshold and you do not think it " +
          s"will go over the deregistration threshold of ${thresholdService.formattedVatDeregThreshold()} in the next 12 months."
      )
    }
  }

  "VatRegistrationException view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- VatRegistrationException.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
        }
      }
    }

    for (option <- VatRegistrationException.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- VatRegistrationException.options.filterNot(_ == option)) {
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
