/*
 * Copyright 2018 HM Revenue & Customs
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

package views.employer.pension

import play.api.data.Form
import forms.employer.pension.WhichPensionSchemeToAddFormProvider
import models.employer.pension.WhichPensionSchemeToAdd
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.pension.whichPensionSchemeToAdd

class WhichPensionSchemeToAddViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whichPensionSchemeToAdd"

  val form = new WhichPensionSchemeToAddFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView =
    () =>
      whichPensionSchemeToAdd(frontendAppConfig, form, WhichPensionSchemeToAdd.options)(serviceInfoContent)(
        fakeRequest,
        messages)

  def createViewUsingForm =
    (form: Form[_]) =>
      whichPensionSchemeToAdd(frontendAppConfig, form, WhichPensionSchemeToAdd.options)(serviceInfoContent)(
        fakeRequest,
        messages)

  "WhichPensionSchemeToAdd view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "WhichPensionSchemeToAdd view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- WhichPensionSchemeToAdd.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- WhichPensionSchemeToAdd.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- WhichPensionSchemeToAdd.options.filterNot(o => o == option)) {
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
