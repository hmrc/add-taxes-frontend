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

package views.employer.paye

import play.api.data.Form
import forms.employer.paye.DoesBusinessHave1To9DirectorsFormProvider
import models.employer.paye.DoesBusinessHave1To9Directors
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.paye.doesBusinessHave1To9Directors

class DoesBusinessHave1To9DirectorsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "doesBusinessHave1To9Directors"

  val form = new DoesBusinessHave1To9DirectorsFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView =
    () => doesBusinessHave1To9Directors(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => doesBusinessHave1To9Directors(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "DoesBusinessHave1To9Directors view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "DoesBusinessHave1To9Directors view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- DoesBusinessHave1To9Directors.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- DoesBusinessHave1To9Directors.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- DoesBusinessHave1To9Directors.options.filterNot(_ == option)) {
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
