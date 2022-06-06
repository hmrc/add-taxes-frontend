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

package views.sa.partnership

import forms.sa.partnership.HaveYouRegisteredPartnershipFormProvider
import models.sa.partnership.HaveYouRegisteredPartnership
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.sa.partnership.haveYouRegisteredPartnership

class HaveYouRegisteredPartnershipViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "haveYouRegisteredPartnership"

  val form = new HaveYouRegisteredPartnershipFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  val saBoolean: Boolean = false

  def createView: () => HtmlFormat.Appendable = () =>
    new haveYouRegisteredPartnership(formWithCSRF,  mainTemplate, conditionalRadio)(frontendAppConfig, saBoolean, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) =>
    new haveYouRegisteredPartnership(formWithCSRF, mainTemplate, conditionalRadio)(frontendAppConfig, saBoolean, form)(serviceInfoContent)(fakeRequest, messages)

  "HaveYouRegisteredPartnership view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain correct heading text" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").text mustBe "Have you already registered your partnership?"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("Have you already registered your partnership?")

      view must include(
        "We will have sent you a Unique Taxpayer Reference (UTR) for your partnership, if you have already registered it.")
    }

    "Render the correct content if saBoolean is false" in {
      val doc = asDocument(createView())
      val view = doc.getElementById("conditional-haveYouRegisteredPartnership.No").text()
      view mustBe "A pdf form will open that you will need to fill in and send back to us before you can add this tax to your account."
    }

    "Render the correct content if saBoolean is true" in {
      val saBoolean:Boolean = true
      def createView: () => HtmlFormat.Appendable = () =>
        new haveYouRegisteredPartnership(formWithCSRF,  mainTemplate, conditionalRadio)(frontendAppConfig, saBoolean, form)(serviceInfoContent)(fakeRequest, messages)

      val doc = asDocument(createView())
      val view = doc.getElementById("conditional-haveYouRegisteredPartnership.No")
      view mustBe null
    }

  }

  "HaveYouRegisteredPartnership view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- HaveYouRegisteredPartnership.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
        }
      }
    }

    for (option <- HaveYouRegisteredPartnership.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButtonWithoutChecked(doc, option.id, "value", option.value)
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
