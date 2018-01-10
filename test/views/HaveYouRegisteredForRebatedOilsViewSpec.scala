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

package views

import forms.HaveYouRegisteredForRebatedOilsFormProvider
import models.{HaveYouRegisteredForRebatedOils, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.haveYouRegisteredForRebatedOils

class HaveYouRegisteredForRebatedOilsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "haveYouRegisteredForRebatedOils"

  val form = new HaveYouRegisteredForRebatedOilsFormProvider()()

  def createView = () => haveYouRegisteredForRebatedOils(frontendAppConfig, form, NormalMode)(HtmlFormat.empty)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) =>
    haveYouRegisteredForRebatedOils(frontendAppConfig, form, NormalMode)(HtmlFormat.empty)(fakeRequest, messages)

  "HaveYouRegisteredForRebatedOils view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "HaveYouRegisteredForRebatedOils view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- HaveYouRegisteredForRebatedOils.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- HaveYouRegisteredForRebatedOils.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- HaveYouRegisteredForRebatedOils.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}