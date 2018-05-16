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

package views.other.charity

import play.api.data.Form
import forms.other.charity.DoYouHaveCharityReferenceFormProvider
import models.other.charity.DoYouHaveCharityReference
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.charity.doYouHaveCharityReference

class DoYouHaveCharityReferenceViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "doYouHaveCharityReference"

  val form = new DoYouHaveCharityReferenceFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => doYouHaveCharityReference(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => doYouHaveCharityReference(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "DoYouHaveCharityReference view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "DoYouHaveCharityReference view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- DoYouHaveCharityReference.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- DoYouHaveCharityReference.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- DoYouHaveCharityReference.options.filterNot(o => o == option)) {
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
