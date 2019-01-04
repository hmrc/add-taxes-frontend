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

package views.sa

import play.api.mvc.Call
import views.behaviours.ViewBehaviours
import forms.sa.YourSaIsNotInThisAccountFormProvider
import models.sa.YourSaIsNotInThisAccount
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.sa.yourSaIsNotInThisAccount

class YourSaIsNotInThisAccountViewSpec extends ViewBehaviours {
  lazy val onSubmit: Call = controllers.sa.routes.YourSaIsNotInThisAccountController.onSubmit()

  val messageKeyPrefix = "yourSaIsNotInThisAccount"

  val form = new YourSaIsNotInThisAccountFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView =
    () => yourSaIsNotInThisAccount(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => yourSaIsNotInThisAccount(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "YourSaIsNotInThisAccountView" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("Your Self Assessment has not been added to an account")
      view must include("You may have used a different business tax account in the past to manage your taxes, duties or schemes online.")
      view must include("Sign in to your other account to add Self Assessment")
      view must include("Add your Self Assessment to this account")
    }
  }

  "YourSaIsNotInThisAccountView" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- YourSaIsNotInThisAccount.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- YourSaIsNotInThisAccount.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- YourSaIsNotInThisAccount.options.filterNot(o => o == option)) {
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
