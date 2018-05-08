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

package views.other.importexports.nes

import controllers.other.importexports.nes.routes._
import play.api.data.Form
import forms.other.importexports.nes.DoYouHaveCHIEFRoleFormProvider
import models.other.importexports.nes.DoYouHaveCHIEFRole
import play.twirl.api.HtmlFormat
import viewmodels.ViewAction
import views.behaviours.ViewBehaviours
import views.html.other.importexports.nes.doYouHaveCHIEFRole

class DoYouHaveCHIEFRoleViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "doYouHaveCHIEFRole"

  val form = new DoYouHaveCHIEFRoleFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => doYouHaveCHIEFRole(frontendAppConfig, form, ViewAction(DoYouHaveCHIEFRoleHasEORIController.onSubmit(), "AddNESHasEori"))(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => doYouHaveCHIEFRole(frontendAppConfig, form, ViewAction(DoYouHaveCHIEFRoleHasEORIController.onSubmit(), "AddNESHasEori"))(serviceInfoContent)(fakeRequest, messages)

  "DoYouHaveCHIEFRole view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "DoYouHaveCHIEFRole view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- DoYouHaveCHIEFRole.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      "contain content" in {
        val doc = asDocument(createView())
        val view = doc.text()

        view must include("This is a code you use to access the Customs Handling of Import and Export " +
          "Freight system (CHIEF) to enter customs information electronically. It’s normally 5 letters, eg ABCDE.")
      }
    }

    for(option <- DoYouHaveCHIEFRole.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- DoYouHaveCHIEFRole.options.filterNot(o => o == option)) {
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
