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

package views.sa.partnership

import play.api.data.Form
import forms.sa.partnership.HaveYouRegisteredPartnershipFormProvider
import models.sa.partnership.HaveYouRegisteredPartnership
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.partnership.haveYouRegisteredPartnership

class HaveYouRegisteredPartnershipViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "haveYouRegisteredPartnership"

  val form = new HaveYouRegisteredPartnershipFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView =
    () => haveYouRegisteredPartnership(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => haveYouRegisteredPartnership(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "HaveYouRegisteredPartnership view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "have-you-registered-partnership"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("Have you already registered your partnership?")

      view must include(
        "We will have sent you a Unique Taxpayer Reference (UTR) for your partnership, if you have already registered it.")
    }
  }

  "HaveYouRegisteredPartnership view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- HaveYouRegisteredPartnership.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- HaveYouRegisteredPartnership.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- HaveYouRegisteredPartnership.options.filterNot(o => o == option)) {
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
