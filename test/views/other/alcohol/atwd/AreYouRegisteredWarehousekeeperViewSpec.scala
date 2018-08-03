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

package views.other.alcohol.atwd

import play.api.data.Form
import forms.other.alcohol.atwd.AreYouRegisteredWarehousekeeperFormProvider
import models.other.alcohol.atwd.AreYouRegisteredWarehousekeeper
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.alcohol.atwd.areYouRegisteredWarehousekeeper

class AreYouRegisteredWarehousekeeperViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "areYouRegisteredWarehousekeeper"

  val form = new AreYouRegisteredWarehousekeeperFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView =
    () => areYouRegisteredWarehousekeeper(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) =>
      areYouRegisteredWarehousekeeper(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "AreYouRegisteredWarehousekeeper view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "AreYouRegisteredWarehousekeeper view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- AreYouRegisteredWarehousekeeper.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      "have content" in {
        val doc = asDocument(createView())
        val view = doc.text()

        view must include("You will have a warehouse approval letter with a warehouse ID if you are.")
      }
    }

    for (option <- AreYouRegisteredWarehousekeeper.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- AreYouRegisteredWarehousekeeper.options.filterNot(o => o == option)) {
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
