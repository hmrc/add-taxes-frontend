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
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.sa.partnership.haveYouRegisteredPartnership

class HaveYouRegisteredPartnershipViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "haveYouRegisteredPartnership"

  val serviceInfoContent: Html = HtmlFormat.empty
  val form: Form[HaveYouRegisteredPartnership] = new HaveYouRegisteredPartnershipFormProvider()()

  val view: haveYouRegisteredPartnership = app.injector.instanceOf[haveYouRegisteredPartnership]

  def applyView(form: Form[HaveYouRegisteredPartnership],
                saBoolean: Boolean = false): HtmlFormat.Appendable =
    view.apply(frontendAppConfig, saBoolean, form)(serviceInfoContent)(fakeRequest, messages)


  val saBoolean: Boolean = false

  val doc: Document = asDocument(applyView(form))

  "HaveYouRegisteredPartnership view" must {
    behave like normalPage(() => applyView(form), messageKeyPrefix)

    "contain correct heading text" in {
      doc.getElementsByTag("h1").text mustBe "Have you already registered your partnership?"
    }

    "Render the correct content" in {
      val view = doc.text()

      view must include("Have you already registered your partnership?")

      view must include(
        "We will have sent you a Unique Taxpayer Reference (UTR) for your partnership, if you have already registered it.")
    }

    "Render the correct conditional content if saBoolean is false" in {
      val view = doc.getElementById("conditional-message").text()
      view mustBe "A pdf form will open that you will need to fill in and send back to us before you can add this tax to your account."
    }

    "Do not render the conditional content if saBoolean is true" in {
      val doc = asDocument(applyView(form, saBoolean = true))
      val view = doc.getElementById("conditional-message")
      view mustBe null
    }

  }

  "render radio buttons for the value" in {
    val doc = asDocument(applyView(form))
    for (option <- HaveYouRegisteredPartnership.options) {
      assertContainsRadioButton(doc = doc, id = option.id, name = "value", value = option.value, isChecked = false)
    }
  }


  for (option <- HaveYouRegisteredPartnership.options) {
    s"rendered with a value of '${option.value}'" must {
      s"have the '${option.value}' radio button selected" in {

        val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value}"))))
        assertContainsRadioButtonWithoutChecked(doc, option.id, "value", option.value)
      }
    }
  }

  "invalid data is sent" must {
    "prepend title with Error: " in {
      val doc = asDocument(applyView(form.bind(Map("value" -> ""))))
      val title = messages("site.service_title", messages(s"$messageKeyPrefix.title"))

      assertEqualsMessage(doc, "title", "error.browser.title", title)
    }
  }
}
