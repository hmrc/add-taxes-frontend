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

package views.other.aeoi

import play.api.data.Form
import forms.other.aeoi.HaveYouRegisteredAEOIFormProvider
import models.other.aeoi.HaveYouRegisteredAEOI
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.aeoi.haveYouRegisteredAEOI

class HaveYouRegisteredAEOIViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "haveYouRegisteredAEOI"

  val form = new HaveYouRegisteredAEOIFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => haveYouRegisteredAEOI(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => haveYouRegisteredAEOI(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "HaveYouRegisteredAEOI view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "have-you-registered-aeoi"
    }
  }

  "HaveYouRegisteredAEOI view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- HaveYouRegisteredAEOI.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    "contain 'You will have an Automatic Exchange of Information ID.'" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "You will have an Automatic Exchange of Information ID.")
    }

    for (option <- HaveYouRegisteredAEOI.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- HaveYouRegisteredAEOI.options.filterNot(o => o == option)) {
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
