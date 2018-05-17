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

package views.other.alcohol.awrs

import play.api.data.Form
import forms.other.alcohol.awrs.SelectAlcoholSchemeFormProvider
import models.other.alcohol.awrs.SelectAlcoholScheme
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.alcohol.awrs.selectAlcoholScheme

class SelectAlcoholSchemeViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "selectAlcoholScheme"

  val form = new SelectAlcoholSchemeFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => selectAlcoholScheme(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => selectAlcoholScheme(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "SelectAlcoholScheme view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "SelectAlcoholScheme view" when {
    "rendered" must {
      "contain radio buttons for the value display the correct labels" in {
        val doc = asDocument(createViewUsingForm(form))
        doc.text() must include("Alcohol & Tobacco Warehousing Declarations (ATWD)")
        doc.text() must include("Alcohol Wholesaler Registration Scheme (AWRS)")

        for (option <- SelectAlcoholScheme.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- SelectAlcoholScheme.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- SelectAlcoholScheme.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))

        assertEqualsMessage(doc, "title", "error.browser.title", messages(s"$messageKeyPrefix.title"))
      }
    }
  }
}
