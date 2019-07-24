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
import forms.vat.ClaimRefundFormProvider
import models.vat.ClaimRefund
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.claimRefund

class ClaimRefundViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "claimRefund"

  val form = new ClaimRefundFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => claimRefund(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => claimRefund(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "ClaimRefund view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "include radio button hint text" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(
        doc,
        "8th directive refunds allow EU businesses to claim VAT refunds incurred in another EU member state. 13th directive refunds let businesses registered outside the EU reclaim VAT paid in the EU."
      )
    }
  }

  "ClaimRefund view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- ClaimRefund.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- ClaimRefund.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- ClaimRefund.options.filterNot(_ == option)) {
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
