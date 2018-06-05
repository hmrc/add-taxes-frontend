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

package views.employer.cis

import play.api.data.Form
import forms.employer.cis.IsYourBusinessInUKFormProvider
import models.employer.cis.IsYourBusinessInUK
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.cis.isYourBusinessInUK

class IsYourBusinessInUKViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "isYourBusinessInUK"

  val form = new IsYourBusinessInUKFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => isYourBusinessInUK(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => isYourBusinessInUK(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "IsYourBusinessInUK view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("Is your business based in the UK?")

      view must include("Yes - my business is based in the UK")

      view must include("No - my business isn’t based in the UK")

    }
  }

  "IsYourBusinessInUK view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- IsYourBusinessInUK.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- IsYourBusinessInUK.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- IsYourBusinessInUK.options.filterNot(o => o == option)) {
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