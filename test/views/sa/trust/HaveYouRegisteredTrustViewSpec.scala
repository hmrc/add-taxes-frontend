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

package views.sa.trust

import play.api.data.Form
import forms.sa.trust.HaveYouRegisteredTrustFormProvider
import models.sa.trust.HaveYouRegisteredTrust
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.trust.haveYouRegisteredTrust

class HaveYouRegisteredTrustViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "haveYouRegisteredTrust"

  val form = new HaveYouRegisteredTrustFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => haveYouRegisteredTrust(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => haveYouRegisteredTrust(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "HaveYouRegisteredTrust view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("Have you registered your trust?")

      view must include(
        "We will have sent you a Unique Taxpayer Reference (UTR) for your trust if you have already registered it.")

    }
  }

  "HaveYouRegisteredTrust view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- HaveYouRegisteredTrust.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- HaveYouRegisteredTrust.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- HaveYouRegisteredTrust.options.filterNot(o => o == option)) {
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
